# Berkeley CS61B_sping2018 Project3 Bear_Maps

  My job for this project is to implement the back end of a web server. To use the program, a user will open an html file in their web browser that displays a map of the city of Berkeley, and the interface will support scrolling, zooming, and route finding (similar to Google Maps). The front end code is provided. My code will be the back end which does all the hard work of figuring out what data to display in the web browser.

  There are three main classes that supports the most functions of the program.

* The ***Rasterer*** class will take as input an upper left latitude and longitude, a lower right latitude and longitude, a window width, and a window height. Using these six numbers, it will produce a 2D array of filenames corresponding to the files to be rendered. 

* The ***GraphDB*** class will read in the Open Street Map dataset and store it as a graph. Each node in the graph will represent a single intersection, and each edge will represent a road. 

* The ***Router*** class will take as input a GraphDB, a starting latitude and longitude, and a destination latitude and longitude, and it will produce a list of nodes that you get from the start point to the end point. As an additional feature, it will be taking that list to generate a sequence of driving instructions that the server will then be able display.
