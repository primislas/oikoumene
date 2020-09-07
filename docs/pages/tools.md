## Tools

[Province Tracer](#map-builder)
* [Command Line Tool](#tracer-command-line)
* [Output Files](#tracer-output-files)

### Map Builder

Objectives: 
* Parse provinces.bmp to a format that can be 
used to construct your own personalized svg. Produced files
contain svg shape outlines with province ids and (if configured)
border outlines for individual border styling.
* Extract game data for building the map into easily digestible JSONs
(such as province data, tag metadata, etc).
* Build fully featured eu4 svg.
* TODO: Rasterize svg map to an image (png or jpeg).

#### Tracer Command Line

You will need to install 
[sbt](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Windows.html) and 
its pre-requisite JDK (see sbt installation guide) to run the scripts.

With your command-line navigate to project's folder. From there you have two options:
run sbt and then run commands from inside sbt REPL, or you can write and invoke one-command
scripts, however that would require you to modify and most likely escape (screen) your
commands. For example:
> sbt -J-Xmx3G -J-Xss3M

> runMain com.lomicron.oikoumene.tools.ClausewitzMapBuilder -gd "D:/Steam/steamapps/common/Europa Universalis IV" -md "C:/Users/primislas/Documents/Paradox Interactive/Europa Universalis IV/mod" -m cool_map -m best_tags -m expanded_timeline

or
> sbt -J-Xmx3G -J-Xss3M "runMain com.lomicron.oikoumene.tools.ClausewitzMapBuilder -gd \"D:/Steam/steamapps/common/Europa Universalis IV\" -md \"C:/Users/primislas/Documents/Paradox Interactive/Europa Universalis IV/mod\" -m cool_map -m best_tags -m expanded_timeline"

To invoke the tracer, execute the following in sbt REPL with necessary options
> runMain com.lomicron.oikoumene.tools.ClausewitzMapBuilder
* --game-dir|-gd <game_dir_path> - provide the path to game dir, e.g. 
> -gd "D:/Steam/steamapps/common/Europa Universalis IV"
* --mod-dir|-md <mod_dir_path> - provide the path to mod dir (root directory containing all the mods),
if you require any mods to be included
> -md "C:/Users/konst/Documents/Paradox Interactive/Europa Universalis IV/mod"
* --mod|-m <mod> - specify a mod (directory) that should be included; if you need several mods,
list them all with individual -m flags:
> -m cool_map -m best_tags -m expanded_timeline
* --output-dir|-od <output_dir> - specify a path to the output directory where produced files will
be written; if omitted, files will be written to ./target/ dir
> -od ~/eu4/svg_map/
* --no-borders|-nb - use this flag if you don't want or need the borders.json (border svg paths)
* --metadata|-meta <engine> - use this flag to include additional province metadata relevant to
selected game engine; currently the only supported game engine is eu4:
> -meta eu4
* --svg|-svg <engine> - use this flag to straight up build a full map svg with all the metadata;
currently the only supported engine is eu4:
> -svg eu4

#### Tracer Output Files

##### provinces.json

**provinces.json** contains province objects that may have the following fields:
* prov_id - province id
* path - string svg path of the shape
* clip - array of svg paths that should be clipped from enclosing shape (to clip shapes 
combine shape path and clip paths into a single svg path and use fill-rule="evenodd" in 
your path tag)

To include additional metadata (owner, controller, religion, terrain, etc), run the tool with
"-meta eu4" key. This will add the following fields to provinces
* owner
* controller
* culture
* culture_group
* religion
* religious_group
* cores
* trade_good
* terrain

##### borders.json 

**borders.json** contains each border segment separating 2 provinces. This allows you to
apply different styles to border sections depending on what kind of a border it is
(area border, sea shore, country border, etc). Each segment may have the following fields:
* l_prov - "left" (inner) province id
* r_prov - "right" (outer) province id; note that r_prov may be missing for sections that
are map borders
* path - string svg path of the border segment

Note that if you don't parse metadata, it's up to you to identify the actual border type.
Additional fields will be included if you run the tracer with "-meta eu4" key:
* border_types - an array of classes that the border belongs to, these could include:
    * border
    * border-country
    * border-country-shore
    * border-land
    * border-land-area
    * border-sea
    * border-sea-area
    * border-sea-shore
    * border-lake
    * border-lake-shore
    * border-undefined
