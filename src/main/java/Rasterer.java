import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    private final int S_L = 288200;

    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        double ullat = params.get("ullat");
        double ullon = params.get("ullon");
        double lrlat = params.get("lrlat");
        double lrlon = params.get("lrlon");
        double w = params.get("w");
        double h = params.get("h");

        Map<String, Object> results = new HashMap<>();

        if (ullat < MapServer.ROOT_LRLAT
                || ullon > MapServer.ROOT_LRLON
                || lrlat > MapServer.ROOT_ULLAT
                || lrlon < MapServer.ROOT_ULLON) {
            results.put("query_success", false);
        } else {
            results.put("query_success", true);

            double resolution = (lrlon - ullon)/w;

            int depth = 1 +
                    (int)(Math.log(((MapServer.ROOT_LRLON - MapServer.ROOT_ULLON)/MapServer.TILE_SIZE/resolution))/Math.log(2));
            depth = Math.min(depth, 7);
            int size = MapServer.TILE_SIZE/(int)Math.pow(2,depth);
            double lon = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON)/Math.pow(2,depth);
            double lat = (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT)/Math.pow(2,depth);

            int starting_x = (int)((ullon - MapServer.ROOT_ULLON)/lon);
            int ending_x = (int)((lrlon - MapServer.ROOT_ULLON)/lon);
            int starting_y = (int)((MapServer.ROOT_ULLAT - ullat)/lat);
            int ending_y = (int)((MapServer.ROOT_ULLAT - lrlat)/lat);

            String[][] grid = new String[ending_y - starting_y + 1][ending_x - starting_x + 1];
            String prefix = "d" + depth + "_x";
            String suffix = ".png";
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    grid[i][j] = prefix + (starting_x+j) + "_y" + (starting_y+i) + suffix;
                }
            }

            results.put("depth", depth);
            results.put("render_grid", grid);

            results.put("raster_ul_lon", MapServer.ROOT_ULLON + lon * starting_x);
            results.put("raster_ul_lat", MapServer.ROOT_ULLAT - lat * starting_y);
            results.put("raster_lr_lon", MapServer.ROOT_ULLON + lon * ending_x + lon);
            results.put("raster_lr_lat", MapServer.ROOT_ULLAT - lat * ending_y - lat);

        }

        return results;
    }

}
