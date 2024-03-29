# Imhof
Pretty geographical maps generation :beginner:

# Description
The goal of the Imhof project, named in honor of the cartographer Eduard Imhof, is to write a program for drawing topographic maps in a style similar to that of Swiss maps, from freely available data. These data come partly from the project OpenStreetMap, and on the other hand from the site viewfinderpanoramas by Jonathan de Ferranti.

The map of Interlaken and its region, shown in Figure 1 below, was generated using the program. The style of this map draws heavily on that of national topographic maps.

<figure>
<img src="https://user-images.githubusercontent.com/9269271/211163610-dc79e75c-a9ba-49ef-a415-414cbb28f03d.png" style="width:600px">
<figcaption align="center">Figure 1: Interlaken and its region. Image generated by the Imhof program</figcaption>
</figure>


<p>For comparison, Figure 2 below shows the official 1:50,000 map for this same region.</p>

<figure>
<img src="https://user-images.githubusercontent.com/9269271/211163626-1c72a450-39b9-4a8b-87da-7d3c02e88b55.png" style="width:600px">
<figcaption align="center">Figure 2: Interlaken and its region (© swisstopo)</figcaption>
</figure>

The program to be produced does not include a graphical interface. It simply takes as input two data files — the first containing the OpenStreetMap data, the second the terrain model — as well as information concerning the area to be drawn, and outputs a map image.

Figure 3 below illustrates the general organization of the program. On the one hand, OpenStreetMap data is loaded from an OSM file (in XML format) then transformed into simple geometric entities (segments lines and polygons) before being drawn to obtain a map without relief. On the other hand, the altimetric information is loaded from a DEM file (in HGT format), then transformed into a relief image. These two images are finally combined to get the final relief map.


<figure>
<img src="https://user-images.githubusercontent.com/9269271/211163634-44f1e563-a8a0-44b7-974a-8e210a7438c5.svg">
<figcaption align="center">Figure 3: Program organization</figcaption>
</figure>


# Contributors
- Dominique Roduit
- Thierry Treyer 
