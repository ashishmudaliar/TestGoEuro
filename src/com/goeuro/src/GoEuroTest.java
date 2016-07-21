package com.goeuro.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoEuroTest {

	public static void main(String[] args) {

		final String URL_STRING = "http://api.goeuro.com/api/v2/position/suggest/en/";
		final String FILE_HEADER = "_id,name,type,latitude,longitude";

		int choice = 1;
		File file = null;

		if(args.length == 0)
		{
			System.out.println("No name provided in arguments");
			return;
		}

		while(choice == 1)
		{
			System.out.println("Please enter name of file (without extension): ");
			Scanner scan = new Scanner(System.in);
			String s = scan.next();
			file = new File(s + ".csv");
			if(file.exists() && !file.isDirectory())
			{
				System.out.println("File already exists. \n Enter 0 to overwrite \n 1 to enter new file name");
				choice = scan.nextInt();
			}
			else
			{
				choice = 0;
			}
		}
		String city = args[0];
		try {
			StringBuilder builder = new StringBuilder();
			URL url = new URL(URL_STRING+city);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append("\n");
			while((line=reader.readLine())!=null)
			{
				builder.append(line);
			}
			JSONArray jsonArray = new JSONArray(builder.toString());
			for(int i=0;i<jsonArray.length();i++)
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject geoObject = jsonObject.getJSONObject("geo_position");
				fileWriter.append(String.valueOf(jsonObject.getLong("_id")));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(jsonObject.getString("name")));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(jsonObject.getString("type")));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(geoObject.getDouble("latitude")));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(geoObject.getDouble("longitude")));
				fileWriter.append("\n");

			}

			fileWriter.flush();
			fileWriter.close();
			System.out.println("File " + file.toString() + " created");

		} catch (MalformedURLException e) {
			System.out.println("Invalid URL");
		} catch (ProtocolException e) {
			System.out.println("Connection error. Please check your internet connection");
		} catch (IOException e) {
			System.out.println("Error in writing file");
		}
	}

}
