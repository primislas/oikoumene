import {ProvinceShape} from "../model/map/province.shape";
import {Border} from "../model/map/border";
import {NameCurve} from "../model/map/name.curve";
import {River} from "../model/map/river";
import {ProvinceListEntity} from "../model/province/province.list.entity";
import {TagMetadata} from "../map/tag.metadata";
import {ProvinceShape3d} from "./province-shape-3d";
import {Renderer, WebGLRenderer, TextureLoader, Texture, RepeatWrapping, Scene, Light, AmbientLight, Camera, PerspectiveCamera, OrthographicCamera, Mesh, PlaneGeometry, ShapeGeometry, Material, MeshStandardMaterial, Shape, Path as ThreePath, Vector2, Vector3, Color} from "three"
import {OrbitControls} from "three/examples/jsm/controls/OrbitControls"
import {GUI} from 'three/examples/jsm/libs/dat.gui.module'
import { LineMaterial } from 'three/examples/jsm/lines/LineMaterial.js';
import { LineGeometry } from 'three/examples/jsm/lines/LineGeometry.js';
import { Line2 } from 'three/examples/jsm/lines/Line2.js';
import {Path} from "../model/map/Path";



export class MapThreeDScene {
    width: number = 5632;
    height: number = 2048;
    style: string = "";
    mode: string = "political";

    riverClasses = ["river-narrowest", "river-narrow", "river-wide", "river-widest"];

    provinceShapes: ProvinceShape3d[] = [];
    borders: Border[] = [];
    names: NameCurve[] = [];
    riversByClass: Map<string, River[]> = new Map<string, River[]>();

    provinces: Map<number, ProvinceListEntity> = new Map<number, ProvinceListEntity>();
    inhabitable: ProvinceShape[] = [];
    tags: Map<string, TagMetadata> = new Map<string, TagMetadata>();

    resolution: Vector2 = new Vector2(100, 100);
    // sizes: any = {width: 100, height: 100};
    canvas: Element;
    renderer: Renderer;
    scene: Scene;
    light: Light;
    camera: Camera;
    textureLoader: TextureLoader;
    guiControls: GUI;

    materials: Map<string, Material> = new Map<string, Material>();

    private MAP_WIDTH = 5632;
    private MAP_HEIGHT = 2048;
    private BEZIER_SCALE = 25;
    private TERRAIN_TEXTURE = "https://raw.githubusercontent.com/primislas/eu4-svg-map/master/resources/colormap-summer-nowater.png";
    private WATER_TEXTURE = "https://raw.githubusercontent.com/primislas/eu4-svg-map/master/resources/colormap-water.png";
    private PROVINCE_OPACITY = 0.35;
    private WATER_OPACITY = 0.1;
    private DEFAULT_PROVINCE_MATERIAL: Material = new MeshStandardMaterial({
        color: "rgb(200,200,200)",
        opacity: this.PROVINCE_OPACITY,
    });
    private DEFAULT_BORDER_MATERIAL: Material = new LineMaterial({
        linewidth: 1,
        color: 0x000000,
        worldUnits: true,
        // opacity: opacity,
        // transparent: true,
        vertexColors: false,
        dashed: false,
        alphaToCoverage: true,
        resolution: this.resolution,
    });

    init() {
        this.setupScene();
        this.setupLight();
        this.setupCamera();
        this.setupGuiControls();
        this.setupDefaultMaterials();
        this.setupMapPlane();
    }

    setupScene() {
        const canvas = document.querySelector('canvas.webgl');
        this.canvas = canvas;
        this.resolution.set(window.innerWidth, window.innerHeight);

        const renderer = new WebGLRenderer({
            canvas: this.canvas,
            antialias: true,
        });
        renderer.setSize(this.resolution.width, this.resolution.height);
        renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
        this.renderer = renderer;

        this.textureLoader = new TextureLoader();

        this.scene = new Scene();
        window.addEventListener('resize', () => this.onResize());
        canvas.addEventListener('wheel', (e) => this.onMouseWheelScroll(e));

        return this.scene;
    }

    setupLight() {
        const light = new AmbientLight( 0xffffff );
        this.light = light;
        this.scene.add( light );
        return light;
    }

    setupCamera() {
        const scene = this.scene;
        const sizes = this.resolution;

        const camera = new PerspectiveCamera(75, sizes.width / sizes.height, 0.1, 10000)
        camera.position.x = 3040;
        camera.position.y = 1600;
        camera.position.z = 1000;
        this.camera = camera;
        scene.add( this.camera );

        return camera;
    }

    setupMapPlane() {
        const plane = new PlaneGeometry(this.width, this.height);
        const texture = this.textureLoader.load( this.TERRAIN_TEXTURE );
        texture.wrapS = RepeatWrapping;
        texture.wrapT = RepeatWrapping;
        const material = new MeshStandardMaterial({map: texture, transparent: true});

        const waterPlane = new PlaneGeometry(this.width, this.height);
        const waterTexture = this.textureLoader.load( this.WATER_TEXTURE );
        waterTexture.wrapS = RepeatWrapping;
        waterTexture.wrapT = RepeatWrapping;
        const waterMaterial = new MeshStandardMaterial({map: waterTexture, transparent: true});

        const water = new Mesh(waterPlane, waterMaterial);
        water.position.set(this.width / 2, this.height / 2, -0.02);

        const background = new Mesh(plane, material);
        background.position.set(this.width / 2, this.height / 2, -0.01);

        this.scene.add( water );
        this.scene.add( background );
        this.render();
    }

    setupDefaultMaterials() {
        this.materials.set("uncolonized", new MeshStandardMaterial({color: "rgb(165, 152, 144)", opacity: 0.2, transparent: true}));
        this.materials.set("wasteland", new MeshStandardMaterial({color: "rgb(145, 132, 124)", opacity: 0, transparent: true}));
        this.materials.set("sea", new MeshStandardMaterial({color: "rgb(157, 239, 254)", opacity: this.WATER_OPACITY, transparent: true}));
        this.materials.set("lake", new MeshStandardMaterial({color: "rgb(135, 248, 250)", opacity: this.WATER_OPACITY, transparent: true}));
        this.materials.set("river", new MeshStandardMaterial({color: "rgb(50, 180, 220)", opacity: this.WATER_OPACITY, transparent: true}));

        this.addBorderMaterial("border", 1, new Color("rgb(50,50,50)"), 0.1);
        this.addBorderMaterial("border-land", 1, new Color("rgb(65,65,65)"), 0.1);
        this.addBorderMaterial("border-map", 0, new Color("rgb(100,50,0)"), 0.4);
        this.addBorderMaterial("border-country", 3, new Color("rgb(50,50,50)"), 0);
        this.addBorderMaterial("border-country-shore", 2, new Color("rgb(50,175,200)"), 0.4);
        this.addBorderMaterial("border-land-area", 1.5, new Color("rgb(50,50,50)"), 0.2);
        this.addBorderMaterial("border-sea", 1, new Color("rgb(0,0,50)"), 0.1);
        this.addBorderMaterial("border-sea-area", 1, new Color("rgb(0,0,50)"), 0.2);
        this.addBorderMaterial("border-sea-shore", 2, new Color("rgb(50,175,200)"), 0.4);
        this.addBorderMaterial("border-lake-shore", 2, new Color("rgb(50,200,200)"), 0.4);
    }

    addBorderMaterial(id: string, lineWidth: number, color: Color, opacity: number) {
        // const mat = new MeshLineMaterial({
        const mat = new LineMaterial({
            linewidth: lineWidth,
            color: color,
            worldUnits: false,
            // opacity: opacity,
            // transparent: true,
            vertexColors: false,
            dashed: false,
            alphaToCoverage: true,
            resolution: this.resolution,
        });
        this.materials.set(id, mat);
    }

    render() {
        console.log("Rendering the world...");
        console.log(`\t${this.provinceShapes.length} province shapes`);
        console.log(`\t${this.tags.size} tags`);
        this.renderer.setClearColor( 0x222222, 1 );
        this.renderer.clearDepth(); // important!
        this.renderer.render(this.scene, this.camera);
    }

    setupGuiControls() {
        const camera = this.camera;

        const gui = new GUI();
        const cameraFolder = gui.addFolder('Camera');
        cameraFolder.add(camera.position, 'x', 0, 5000).onChange(() => this.render());
        cameraFolder.add(camera.position, 'y', 0, 2048).onChange(() => this.render());
        cameraFolder.add(camera.position, 'z', 0, 2000).onChange(() => this.render());
        cameraFolder.open();

        this.guiControls = gui;
    }

    setupOrbitControls(camera: Camera, renderer: Renderer) {
        const controls = new OrbitControls( camera, renderer.domElement );
        controls.listenToKeyEvents( window ); // optional
        controls.addEventListener( 'change', () => this.render() ); // call this only in static scenes (i.e., if there is no animation loop)
        // controls.enableDamping = true; // an animation loop is required when either damping or auto-rotation are enabled
        // controls.dampingFactor = 0.05;
        controls.screenSpacePanning = false;
        controls.minDistance = 1;
        controls.maxDistance = 4000;
        controls.maxPolarAngle = Math.PI / 2;
    }

    onResize() {
        this.resolution.set( window.innerWidth, window.innerHeight );

        // Update camera
        this.camera.aspect = this.resolution.width / this.resolution.height;
        this.camera.updateProjectionMatrix();

        // Update renderer
        this.renderer.setSize(this.resolution.width, this.resolution.height);
        this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));

        this.materials.forEach((mat, id) => {
            if (id.startsWith("border"))
                mat.resolution.set(this.resolution.width, this.resolution.height);
        });

        this.render();
    }

    onMouseWheelScroll(event): void {
        console.log(event.deltaY);
    }

    shapeToMesh(prov: ProvinceShape3d): Mesh {
        const shape = this.provinceToShape(prov);
        shape.holes = this.provinceClipPaths(prov);
        const material = this.DEFAULT_PROVINCE_MATERIAL;
        const geom = new ShapeGeometry(shape);
        return new Mesh(geom, material);
    }

    provinceToShape(prov: ProvinceShape3d): Shape {
        const startingPath = prov.path[0];
        const startP = (startingPath.polyline || startingPath.bezier)[0];
        const [startX, startY] = [startP[0], startP[1]];
        const points = prov.path
            .map(elem => Path.asPoints(elem, this.BEZIER_SCALE).slice(1))
            .reduce((acc, a) => acc.concat(a), []);

        const shape = new Shape();
        shape.moveTo(startX, startY);
        points.forEach(p => shape.lineTo(p[0], p[1]));

        return shape;
    }

    provinceClipPaths(prov: ProvinceShape3d): ThreePath[] {
        return (prov.clip || [])
            .map(curve => {
                const start = (curve[0].bezier || curve[0].polyline)[0];
                const points = curve
                    .map(segment => Path.asPoints(segment, this.BEZIER_SCALE))
                    .map(segment => segment.slice(1))
                    .reduce((acc, a) => acc.concat(a), []);

                const hole = new ThreePath();
                hole.moveTo(start[0], start[1]);
                points.forEach(p => hole.lineTo(p[0], p[1]));
                return hole;
            });
    }

    addShapes(shapes: ProvinceShape3d[]) {
        const shapesWithMesh = shapes
            .map(shape => {
                shape.mesh = this.shapeToMesh(shape);
                return shape;
            });
        this.provinceShapes = this.provinceShapes.concat(shapesWithMesh);

        if (this.provinces.size > 0)
            this.addProvinceMetadataToNewShapes(shapesWithMesh, this.provinces);

        return this;
    }

    addProvinceMetadata(provinces: ProvinceListEntity[]) {
        this.provinces = provinces
            .reduce(
                (acc, p) => {
                    acc.set(p.id, p);
                    return acc;
                },
                this.provinces
            );

        if (this.provinceShapes.length > 0)
            this.addNewProvinceMetadataToShapes(this.provinceShapes, provinces);

        return this;
    }

    addTags(tags: TagMetadata[]) {
        this.tags = tags
            .reduce(
                (acc, t) => {
                    acc.set(t.id, t);
                    return acc;
                },
                this.tags
            );
        this.materials = tags
            .reduce(
                (acc, t) => {
                    if (t.color) {
                        const mat = new MeshStandardMaterial({
                            color: "rgb(" + `${t.color.r}, ${t.color.g}, ${t.color.b}` + ")",
                            transparent: true,
                            opacity: this.PROVINCE_OPACITY,
                        });
                        acc.set(t.id, mat);
                    }
                    return acc;
                },
                this.materials
            );

        return this;

    }

    addBorders(borders: Border[]) {
        this.borders = borders
            // .filter(b => b.type === "border-country")
            // .slice(0, 50)
            .map(b => {
                const vertices = Border
                    .asPoints(b, this.BEZIER_SCALE)
                    .map(p => new Vector3(p[0], this.MAP_HEIGHT - p[1], 0.01));

                const g = new LineGeometry();
                const positions = vertices.map(v => [v.x, v.y, v.z]).reduce((acc, a) => acc.concat(a), []);
                g.setPositions(positions);
                const material = this.materials.get(b.type) || this.DEFAULT_BORDER_MATERIAL;
                const line = new Line2(g, material);
                line.computeLineDistances();

                b.mesh = line;
                return b;
            });

        this.borders
            .filter(b => b.mesh)
            // .filter(b => b.type === "border-country")
            .forEach(b => this.scene.add(b.mesh));

        return this;
    }

    private addNewProvinceMetadataToShapes(shapes: ProvinceShape3d[], provinces: ProvinceListEntity[]) {
        // TODO: optimize to only iterate by new provinces
        return this.mergeProvinceMetadata();
    }

    private addProvinceMetadataToNewShapes(shapes: ProvinceShape3d[], provinces: Map<number, ProvinceListEntity>) {
        // TODO: optimize to only iterate by new provinces
        return this.mergeProvinceMetadata();
    }

    private mergeProvinceMetadata() {
        this.provinceShapes
            .map(shape => this.addShapeMetadata(shape, this.provinces))
            .map(shape => {
                if (shape.metadata.owner) {
                    const owner = shape.metadata.owner;
                    const ownerMat = this.materials.get(owner);
                    if (ownerMat)
                        shape.mesh.material = ownerMat;
                } else if (shape.metadata.type) {
                    const type = shape.metadata.type;
                    let mat;
                    if (type === "province") {
                        if ((shape.metadata.climate || []).find(c => c === "impassable"))
                            mat = this.materials.get("wasteland");
                        else if (shape.metadata.is_city === false)
                            mat = this.materials.get("uncolonized");
                    } else
                        mat = this.materials.get(type);
                    if (mat)
                        shape.mesh.material = mat;
                }
                return shape;
            })
            .forEach(shape => this.scene.add( shape.mesh ));
        this.render();
        return this;
    }

    private addShapeMetadata(
        shape: ProvinceShape3d,
        provinces: Map<number, ProvinceListEntity> = new Map<number, ProvinceListEntity>()
    ): ProvinceShape3d {
        shape.metadata = provinces.get(shape.provId);
        return shape;
    }

}
