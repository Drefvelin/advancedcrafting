package net.tfminecraft.AdvancedCrafting.Database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.tfminecraft.AdvancedCrafting.Loaders.HitLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.SchemeLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.TypeLoader;
import net.tfminecraft.AdvancedCrafting.Managers.AlloyManager;
import net.tfminecraft.AdvancedCrafting.Objects.Alloys.Alloy;
import net.tfminecraft.AdvancedCrafting.Objects.Alloys.AlloyStation;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.Hits.CraftingHit;
import net.tfminecraft.AdvancedCrafting.Objects.Data.AlloyData;
import net.tfminecraft.AdvancedCrafting.Objects.Data.StatData;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.Ingredient;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.IngredientType;
import net.tfminecraft.AdvancedCrafting.Objects.Schemes.ModelScheme;
import net.tfminecraft.AdvancedCrafting.Objects.Stats.StatModifier;

public class AlloyDatabase {
	private JSONObject json; // org.json.simple
    JSONParser parser = new JSONParser();
    public void loadAlloys() {
    	File folder = new File("plugins/AdvancedCrafting/data/alloys");
    	for(final File file : folder.listFiles()) {
    		if(!file.isDirectory()) {
    			try {
    				json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    				String id = (String) json.get("id");
    				String name = (String) json.get("name");
    				int model = (int) Math.round((Double) json.get("model"));
    				IngredientType type = TypeLoader.getIngredientTypeByString((String) json.get("type"));
    				ModelScheme scheme = SchemeLoader.getModelSchemeByString((String) json.get("scheme"));
    				StatData stats = new StatData();
    				int i = 0;
    				JSONArray statArray = (JSONArray) json.get("stats");
    				while(i < statArray.size()) {
    					String s = statArray.get(i).toString();
    					String st = s.split("\\(")[0];
    					double amount = Double.parseDouble(s.split("\\(")[1].replace(")", ""));
    					stats.addModifier(new StatModifier(st, amount));
    					i++;
    				}
    				HashMap<CraftingHit, Integer> hits = new HashMap<>();
    				i = 0;
    				JSONArray hitArray = (JSONArray) json.get("hits");
    				while(i < hitArray.size()) {
    					String s = hitArray.get(i).toString();
    					String hit = s.split("\\.")[0];
    					int amount = Integer.parseInt(s.split("\\.")[1]);
    					hits.put(HitLoader.getByString(hit), amount);
    					i++;
    				}
    				AlloyManager.addAlloy(new Alloy(id, name, new AlloyData(model, type, scheme, stats, hits)));
    			} catch (Exception ex) {
    				ex.printStackTrace();
    			}
    		}
    	}
	}
    
    public String getResult(AlloyStation s) {
    	File folder = new File(getPath(s));
		if(!folder.exists()) folder.mkdirs();
		File file = new File(getPath(s), "recipe.json");
		if (file.exists()) {
        	try {
				json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				return (String) json.get("result");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
        }
		return null;
	}
	public void saveRecipe(AlloyStation s, String result) {
		try {
			File folder = new File(getPath(s));
			if(!folder.exists()) folder.mkdirs();
			File file = new File(getPath(s), "recipe.json");
			if(file.exists() == true) {
				file.delete();
			}
			file.createNewFile();
        	PrintWriter pw = new PrintWriter(file, "UTF-8");
        	pw.print("{");
        	pw.print("}");
        	pw.flush();
        	pw.close();
            HashMap<String, Object> defaults = new HashMap<String, Object>();
        	json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        	defaults.put("result", result);
        	save(file, defaults);
        } catch (Throwable ex) {
			ex.printStackTrace();
        }
    }
	private String getPath(AlloyStation station) {
		String path = "plugins/AdvancedCrafting/data/alloy-recipes";
		if(station.getBaseItem() != null) {
			path = path+"/"+station.getBaseItem().getId();
		}
		List<String> c = new ArrayList<>();
		for(Ingredient i : station.getCatalysts()) {
			c.add(i.getId());
		}
		Collections.sort(c);
		for(String s : c) {
			path = path+"/"+s;
		}
		return path;
	}
	@SuppressWarnings("unchecked")
	public void saveAlloy(Alloy a) {
		try {
			File file = new File("plugins/AdvancedCrafting/data/alloys",a.getId()+".json");
			if(file.exists() == true) {
				file.delete();
			}
			file.createNewFile();
        	PrintWriter pw = new PrintWriter(file, "UTF-8");
        	pw.print("{");
        	pw.print("}");
        	pw.flush();
        	pw.close();
            HashMap<String, Object> defaults = new HashMap<String, Object>();
        	json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        	defaults.put("id", a.getId());
        	defaults.put("name", a.getName());
        	defaults.put("model", a.getData().getModel());
        	defaults.put("type", a.getData().getType().getId());
        	defaults.put("scheme", a.getData().getModelScheme().getId());
        	int i = 0;
        	JSONArray statArray = new JSONArray();
        	while(i < a.getData().getStatData().getModifiers().size()) {
        		String stat = a.getData().getStatData().getModifiers().get(i).getType()+ "("+ a.getData().getStatData().getModifiers().get(i).getAmount()+")";
        		statArray.add(stat);
        		i++;
        	}
        	defaults.put("stats", statArray);
        	JSONArray hitArray = new JSONArray();
        	for(CraftingHit h : a.getData().getHits().keySet()) {
        		String hit = h.getId()+"."+a.getData().getHits().get(h);
        		hitArray.add(hit);
        	}
        	defaults.put("hits", hitArray);
        	save(file, defaults);
        } catch (Throwable ex) {
			ex.printStackTrace();
        }
    }
	@SuppressWarnings("unchecked")
    public boolean save(File file, HashMap<String, Object> defaults) {
      try {
    	  JSONObject toSave = new JSONObject();
      
        for (String s : defaults.keySet()) {
          Object o = defaults.get(s);
          if (o instanceof String) {
            toSave.put(s, getString(s, defaults));
          } else if (o instanceof Double) {
            toSave.put(s, getDouble(s, defaults));
          } else if (o instanceof Integer) {
            toSave.put(s, getInteger(s, defaults));
          } else if (o instanceof JSONObject) {
            toSave.put(s, getObject(s, defaults));
          } else if (o instanceof JSONArray) {
            toSave.put(s, getArray(s, defaults));
          }
        }
      
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        treeMap.putAll(toSave);
      
       Gson g = new GsonBuilder().setPrettyPrinting().create();
       String prettyJsonString = g.toJson(treeMap);
      
        FileWriter fw = new FileWriter(file);
        fw.write(prettyJsonString);
        fw.flush();
        fw.close();
      
        return true;
      } catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }
    }
    
    public String getRawData(String key, HashMap<String, Object> defaults) {
        return json.containsKey(key) ? json.get(key).toString()
           : (defaults.containsKey(key) ? defaults.get(key).toString() : key);
      }
    
      public String getString(String key, HashMap<String, Object> defaults) {
        return ChatColor.translateAlternateColorCodes('&', getRawData(key, defaults));
      }

      public boolean getBoolean(String key, HashMap<String, Object> defaults) {
        return Boolean.valueOf(getRawData(key, defaults));
      }

      public double getDouble(String key, HashMap<String, Object> defaults) {
        try {
          return Double.parseDouble(getRawData(key, defaults));
        } catch (Exception ex) { }
        return -1;
      }

      public double getInteger(String key, HashMap<String, Object> defaults) {
        try {
          return Integer.parseInt(getRawData(key, defaults));
        } catch (Exception ex) { }
        return -1;
      }
     
      public JSONObject getObject(String key, HashMap<String, Object> defaults) {
         return json.containsKey(key) ? (JSONObject) json.get(key)
           : (defaults.containsKey(key) ? (JSONObject) defaults.get(key) : new JSONObject());
      }
     
      public JSONArray getArray(String key, HashMap<String, Object> defaults) {
    	     return json.containsKey(key) ? (JSONArray) json.get(key)
    	       : (defaults.containsKey(key) ? (JSONArray) defaults.get(key) : new JSONArray());
      }
}
