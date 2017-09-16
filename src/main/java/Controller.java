import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jack on 07/11/2016.
 */
public class Controller  implements RequestHandler<String, String> {

    public static void main(String[] args) {
        Controller controller = new Controller();

        System.out.println(controller.handleRequest("hi my name is jack", null));
    }
    public String handleRequest(String inpu, Context context) {
        ClassLoader classLoader = getClass().getClassLoader();
        String[] lines = inpu.split("\n");
        ArrayNode meters = JsonNodeFactory.instance.arrayNode();
        //Map<String, Double>[] mete
        // rs = new TreeMap[lines.length];
        int i = 0;
        String command = "";
        for (String line : lines) {
          //  meters[i] = new TreeMap<String, Double>();
            ObjectNode node = JsonNodeFactory.instance.objectNode();

            String[] tokens = line.split(" ");
            StringBuilder input = new StringBuilder();
            for (String token : tokens) {
                if (token.trim().isEmpty()) {
                    continue;
                }
                input.append("\"" + token.toUpperCase() + "\" ");
            }

            command = "echo '" + input.toString().trim() + "' | ./src/main/resources/graehl/carmel/bin/linux/carmel -sliOEQk 5 ./src/main/resources/graehl/carmel/bin/macosx/wfst005.full.txt";




            String[] cmd = new String[]{
                    "/bin/sh", "-c",
                    command};
            Process pr = null;
            try {
                pr = Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                return e.getMessage();
            }


            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(pr.getInputStream()));
            StringBuilder output = new StringBuilder();
            String meter = "";
            try {
                while ((meter = reader.readLine()) != null) {
                    String[] parts = meter.split("\\.");
                    if (!parts[0].equals("0")) {
                        String ictusX = parts[0].substring(0, parts[0].length() - 2)
                                .replaceAll(" ", "")
                                .replaceAll("S\\*", "*")
                                .replaceAll("S", "/");//.substring(0, parts[0].length()-2).replaceAll("S ", "/").replaceAll("S\\* ?", "*");

                        node.put(ictusX, Double.parseDouble("0." + parts[1]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
            meters.add(node);
        }
        String output = command;
        if (meters.size() != 0) output = meters.toString();

        return output;
    }
}
