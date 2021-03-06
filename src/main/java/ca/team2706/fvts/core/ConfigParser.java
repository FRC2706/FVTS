package ca.team2706.fvts.core;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConfigParser {

	public static Map<String, Map<String, String>> getProperties(File f, String name) throws Exception {

		List<String> lines = new ArrayList<String>();

		Scanner in = new Scanner(f);

		while (in.hasNextLine()) {
			lines.add(in.nextLine());
		}

		in.close();

		int startLine = 0, endLine = 0;

		boolean foundStart = false;

		for (int i = 0; i < lines.size(); i++) {

			String s = lines.get(i);

			if (s.equals("")) {
				continue;
			}

			if (!foundStart) {
				if (s.trim().equals(name + ":")) {
					startLine = i;
					foundStart = true;
				}

			} else {

				if (!s.startsWith("  ")) {
					endLine = i;
					break;
				}

			}

		}
		if (endLine == 0) {
			endLine = lines.size();
		}

		Map<String, Map<String, String>> properties = new HashMap<String, Map<String, String>>();

		String sectionName = null;
		Map<String, String> curr = null;
		for (int i = startLine + 1; i < endLine; i++) {

			// Get the line and remove the spaces
			String s = lines.get(i).substring(2);
			if (sectionName == null && s.endsWith(":")) {
				sectionName = s.substring(0, s.length() - 1);
				curr = new HashMap<String, String>();
			} else if (sectionName != null) {
				s = s.substring(2);
				if (s.split("=").length >= 2) {

					String key = s.split("=")[0];
					String value = s.split("=", 2)[1];

					curr.put(key, value);
				} else {
					properties.put(sectionName, curr);
					sectionName = null;
					i--;
				}
			}

		}
		if(sectionName != null) {
			properties.put(sectionName, curr);
		}

		return properties;

	}

	public static Map<String, String> getPropertiesM(File f, String name) throws Exception {

		List<String> lines = new ArrayList<String>();

		Scanner in = new Scanner(f);

		while (in.hasNextLine()) {
			lines.add(in.nextLine());
		}

		in.close();

		int startLine = 0, endLine = 0;

		boolean foundStart = false;

		for (int i = 0; i < lines.size(); i++) {

			String s = lines.get(i);

			if (s.equals("")) {
				continue;
			}

			if (!foundStart) {
				if (s.trim().equals(name + ":")) {
					startLine = i;
					foundStart = true;
				}

			} else {

				if (!s.startsWith("  ")) {
					endLine = i;
					break;
				}

			}

		}
		if (endLine == 0) {
			endLine = lines.size();
		}

		Map<String, String> properties = new HashMap<String, String>();
		for (int i = startLine + 1; i < endLine; i++) {

			// Get the line and remove the spaces
			String s = lines.get(i).substring(2);
			if (s.split("=").length >= 2) {

				String key = s.split("=")[0];
				String value = s.split("=", 2)[1];

				properties.put(key, value);
			}
		}

		return properties;

	}

	public static List<String> listLists(File f) throws Exception {

		Scanner in = new Scanner(f);
		List<String> lines = new ArrayList<String>();

		while (in.hasNextLine()) {
			lines.add(in.nextLine());
		}

		in.close();

		List<String> lists = new ArrayList<String>();

		for (String s : lines) {

			if (s.equals("")) {
				continue;
			}

			if (!s.startsWith("  ")) {

				String name = s.trim().substring(0, s.trim().length() - 1);

				lists.add(name);

			}

		}

		return lists;

	}

	public static void saveList(File f, String name, Map<String, Map<String, String>> data) throws Exception {

		Scanner in = new Scanner(f);

		List<String> lines = new ArrayList<String>();

		while (in.hasNextLine()) {

			lines.add(in.nextLine());

		}

		in.close();

		int startLine = 0, endLine = 0;

		boolean foundStart = false;

		for (int i = 0; i < lines.size(); i++) {

			String s = lines.get(i);

			if (s.equals("")) {
				continue;
			}

			if (!foundStart) {

				if (s.equals(name + ":")) {
					startLine = i;
					foundStart = true;
				}

			} else {

				if (!s.startsWith("  ")) {
					endLine = i;
					break;
				}

			}

		}

		if (endLine == 0) {
			endLine = lines.size();
		}

		List<String> newLines = new ArrayList<String>();

		if (foundStart) {

			for (int i = 0; i < lines.size(); i++) {

				if (i < startLine || i > endLine) {
					newLines.add(lines.get(i));
				}

			}
		} else {
			newLines = lines;
		}

		// We have the fjson file minus the list we want to save

		newLines.add(name + ":");

		for (String key : data.keySet()) {
			newLines.add("  " + key + ":");
			for (String key2 : data.get(key).keySet()) {
				String value = data.get(key).get(key2);

				newLines.add("    " + key2 + "=" + value);
			}
		}

		// Now we need to save it

		f.delete();
		f.createNewFile();

		PrintWriter out = new PrintWriter(new FileWriter(f, true));

		for (String s : newLines) {
			out.println(s);
		}

		out.close();

	}

}
