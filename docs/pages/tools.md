## Tools

1. [Prerequisites](#prerequisites)
1. [Map Builder](#map-builder)
    * [Command Line Tool](#tracer-command-line)
    * [Output Files](#tracer-output-files)
    * [Rasterize SVG (svg -> png)](#rasterize-svg)
1. [Save to JSON](#save-to-json)

### Prerequisites

You will need to install 
[sbt](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Windows.html) and 
its pre-requisite JDK (see sbt installation guide) to run the scripts.
I also recommend [Git Bash](https://gitforwindows.org/) as a bash emulator
(command line utility) for Windows.

Clone game repo and navigate to the directory:
> git clone https://github.com/primislas/oikoumene.git oikoumene
>
> cd oikoumene

### Map Builder

Objectives: 
* Parse provinces.bmp to a format that can be 
used to construct your own personalized svg. Produced files
contain svg shape outlines with province ids and (if configured)
border outlines for individual border styling.
* Extract game data for building the map into easily digestible JSONs
(such as province data, tag metadata, etc).
* Build fully featured eu4 svg map.
* Build maps from save games.
* Rasterize svg map to an image (e.g. png or jpeg).

#### Tracer Command Line

You have two options from the project directory:
1. run sbt and then run commands from inside sbt REPL
1. write and invoke one-command scripts, however that would require you 
to modify and most likely escape (screen) your commands. For example:
> sbt -J-Xmx3G -J-Xss3M

> runMain com.lomicron.eu4.tools.ClausewitzMapBuilder -gd "D:/Steam/steamapps/common/Europa Universalis IV" -md "C:/Users/primislas/Documents/Paradox Interactive/Europa Universalis IV/mod" -m cool_map -m best_tags -m expanded_timeline

or
> sbt -J-Xmx3G -J-Xss3M "runMain com.lomicron.eu4.tools.ClausewitzMapBuilder -gd \\"D:/Steam/steamapps/common/Europa Universalis IV\\" -md \\"C:/Users/primislas/Documents/Paradox Interactive/Europa Universalis IV/mod\\" -m cool_map -m best_tags -m expanded_timeline"

To invoke the tracer, execute the following in sbt REPL with necessary options
> runMain com.lomicron.eu4.tools.ClausewitzMapBuilder
* --game-dir|-gd <game_dir_path> - provide the path to game dir, e.g. 
> -gd "D:/Steam/steamapps/common/Europa Universalis IV"
* --mod-dir|-md <mod_dir_path> - provide the path to mod dir (root directory containing all the mods),
if you require any mods to be included, in combination with individual mod folders (see --mod below)
> -md "C:/Users/saymyname/Documents/Paradox Interactive/Europa Universalis IV/mod"
* --mod|-m <mod> - specify a mod (directory) that should be included; if you need several mods,
list them all with individual -m flags:
> -m cool_map -m best_tags -m expanded_timeline
* --cache-dir|-cd <cache_dir> - specify a path to a dir where map metadata cache will be stored to speed
up subsequent generations. If no files in cache dir exist, game data will be parsed and relevant data
written here. Otherwise if you need to rebuild the cache, run builder with added -rc command (see below).
> -cd "C:/Users/saymyname/Documents/Paradox Interactive/Europa Universalis IV/mod/map_cache"
* --rebuild-cache|-rc - use this flag to rebuild a cache (e.g. after a mod update or to keep using 
the same cache directory)
* --output-dir|-od <output_dir> - specify a path to the output directory where produced files will
be written; if omitted, files will be written to ./target/ dir
> -od ~/eu4/svg_map/
* --no-borders|-nb - use this flag if you don't want or need the borders.json (border svg paths) and/or
borders in svg map
* --no-rivers|-nr - use this flag if you don't want or need the rivers.json (river svg paths) and/or
rivers in svg map
* --no-names|-nn - use this flag if you don't want or need (tag) names on produced svg map
* --no-wastelands|-nw - use this flag if you don't want owned (colored) wastelands on produced svg map
* --metadata|-meta <engine> - use this flag to include additional province metadata relevant to
selected game engine; currently the only supported game engine is eu4:
> -meta eu4
* --svg|-svg <engine> - use this flag to straight up build a full map svg with all the metadata;
currently the only supported engine is eu4:
> -svg eu4
* --background|-bg <season> - use this flag to add terrain and water backgrounds to your map, 
default season is **summer**, backgrounds are only added if the map is deemed to have been unmodified
> bg winter
* --map-mode|-mode <map_mode> - use this toggle to select map mode, default map mode is **political**,
currently supported map modes are: political, province_outline (use this to generate an "empty" map)
> -mode province_outline
* --save-game|-save <path_to_save_.eu4_file> - save file will be parsed and its data applied to produced map
> -save "C:/Users/saymyname/Documents/Paradox Interactive/Europa Universalis IV/save games/Ironman.eu4"

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

#### Rasterize SVG

Generally speaking, produced map turned out to be too big to be converted to an image 
by existing online converters. Furthermore, if you build an svg with names, none of those
services will be able to preserve names in resulting image. So the best approach is to 
open the map in Chrome or Firefox and capture the page as an image.
* Chrome has it hidden:
    1. open the map
    1. _Ctrl + Shift + I_ to open dev tools
    1. _Ctrl + Shift + P_ to open dev console
    1. type in _screenshot_ and select "Capture full size screenshot"
* In Firefox:
    1. open the map
    1. _Ctrl + Shift + K_ to open dev console
    1. type in _:screenshot --fullpage_
    1. alternatively copy to clipboard with _:screenshot --fullpage --clipboard_
    
Additionally, you could use chrome (and firefox) from command line to do the conversion:
> "C:/Program Files (x86)/Google/Chrome/Application/chrome" --headless --window-size=5632,2048 --screenshot="D:/eu4_political.png" "file:///D:/eu4_political.svg" 

### Save to JSON

Parses and stores a save game file as json. Note that the tool will create a new folder in your specified
output directory named after the save file (e.g. BBB.eu4 will generate a BBB folder) and split the save into
multiple json files so as to make them more digestible. (Most editors would struggle with a single huge text file.)

Navigate to oikoumene project folder and run
> sbt -J-Xmx3G -J-Xss3M "runMain com.lomicron.eu4.tools.SaveGameToJson -save \"C:/Users/username/Documents/Paradox Interactive/Europa Universalis IV/save games/My Campaign.eu4\" -od \"C:/Users/username/Documents/Paradox Interactive/Europa Universalis IV/mod/save_editor\""
