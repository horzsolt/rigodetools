package horzsolt.rigodetools.mp3.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * Created by horzsolt on 2017. 06. 17..
 */
public class AlbumSerializer {


    public static void serialize(Album album) {
        try (Writer writer = new FileWriter("g:\\gson\\" + album.getTitle() + ".json")) {
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(album);
            writer.write(json);
        } catch (IOException io) {
            System.out.println("Couldn't create file: " + "g:\\gson\\" + album.getTitle());
        }
    }

}
