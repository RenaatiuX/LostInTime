package com.ren.lostintime.client.screen.book.util;

import com.ren.lostintime.LostInTime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapDrawUtils {

    public static List<DiscoveredLocation> loadLocations(ResourceLocation csvPath) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        List<DiscoveredLocation> locations = new ArrayList<>();

        try {
            // Get the resource from the manager (Returns Optional in 1.20.1)
            Optional<Resource> resourceOpt = manager.getResource(csvPath);

            if (resourceOpt.isPresent()) {
                // Open the stream in a try-with-resources block so it closes automatically
                try (InputStream stream = resourceOpt.get().open();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                    String line;
                    // Read line by line
                    while ((line = reader.readLine()) != null) {
                        // Skip headers or empty lines
                        if (line.trim().isEmpty() || line.startsWith("Entity") || line.startsWith("Title")) continue;

                        // Assuming CSV format: EntityName,Latitude,Longitude
                        String[] columns = line.split(";");
                        if (columns.length >= 3) {
                            String discoverLocationName = columns[0].trim();
                            String cityLocationName = columns[1].trim();
                            var coordinates = columns[2].trim().split("/");
                            if (coordinates.length != 2) {
                                LostInTime.LOGGER.warn("cant read coordinates: {}", columns[2]);
                            } else {
                                float lat = Float.parseFloat(coordinates[0].trim());
                                float lon = Float.parseFloat(coordinates[1].trim());
                                locations.add(new DiscoveredLocation(new Vec2(lat, lon), discoverLocationName, cityLocationName));
                            }


                        } else {
                            LostInTime.LOGGER.warn("cant read csv line because it has too few columns: {}", line);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Always catch exceptions when doing file I/O
            e.printStackTrace();
        }
        return locations;
    }

    public static float sanitize(float value, float circleGroupOrder){

        float max = circleGroupOrder / 2f;
        float min = -max;

        float sanitized = value;

        while (sanitized > max){
            sanitized -= circleGroupOrder;
        }

        while (sanitized < min){
            sanitized += circleGroupOrder;
        }
        return sanitized;
    }


    /**
     * Converts real-world GPS coordinates to the normalized map coordinates (0.0 to 1.0).
     *
     * @param latitude  North/South position (-90.0 to 90.0). Positive is North, Negative is South.
     * @param longitude East/West position (-180.0 to 180.0). Positive is East, Negative is West.
     * @return An array containing [locX, locY] for the MapLocationComponent.
     */
    public static float[] projectRealWorldToMap(float latitude, float longitude) {
        // Clamp the values just in case invalid GPS coordinates are passed



        //latitude = sanitize(latitude, 180);
        //longitude = sanitize(longitude, 360);

        // Longitude: -180 is the far left (0.0), +180 is the far right (1.0)
        float locX = (longitude + 180.0f) / 360.0f;

        // Latitude: +90 is the top (0.0), -90 is the bottom (1.0)
        // Note: Y is inverted because in Minecraft GUI, Y=0 is the top of the screen!
        //float locY = (-latitude + 90.0f) / 180.0f;

        float locY = 0.5f * (1f - (float)Math.tan(latitude / 2d));


        return new float[]{locX, locY};
    }

    public static float[] projectRealWorldToMap(float latitude, float longitude, int imagWidth, int imageHeight) {
        var location = projectRealWorldToMap(latitude, longitude);
        location[0] *= (float)imagWidth;
        location[1] *= (float)imageHeight;
        return location;
    }
}
