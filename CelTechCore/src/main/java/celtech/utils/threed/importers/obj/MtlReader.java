/*
 * Copyright (c) 2010, 2013 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package celtech.utils.threed.importers.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

/**
 * Reader for OBJ file MTL material files.
 */
public class MtlReader
{

    private String baseUrl;

    /**
     *
     * @param filename
     * @param filePath
     */
    public MtlReader(final String filename, final String filePath)
    {
        String fileUrl = filePath + "/" + filename;
        try
        {
            URL mtlUrl = new URL(fileUrl);
//            log("Reading material from filename = " + mtlUrl);
            read(mtlUrl.openStream());
        } catch (FileNotFoundException ex)
        {
            System.err.println("No material file found for obj. [" + fileUrl + "]");
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private Map<PhongMaterial, Set<String>> materials = new HashMap<>();
    private boolean modified = false;

    private void read(InputStream inputStream) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String name = "default";
        PhongMaterial currentMaterial = null;

        while ((line = br.readLine()) != null)
        {
            try
            {
                if (line.isEmpty() || line.startsWith("#"))
                {
                    // comments and empty lines are ignored
                } else if (line.startsWith("newmtl "))
                {
                    if (currentMaterial != null)
                    {
                        addMaterial(name, currentMaterial);
                    }
                    currentMaterial = new PhongMaterial();
                    name = line.substring("newmtl ".length());
                } else if (line.startsWith("Kd "))
                {
                    currentMaterial.setDiffuseColor(readColor(line.substring(3)));
                    modified = true;
                } else if (line.startsWith("Ks "))
                {
                    currentMaterial.setSpecularColor(readColor(line.substring(3)));
                    modified = true;
                } else if (line.startsWith("Ns "))
                {
                    currentMaterial.setSpecularPower(Double.parseDouble(line.substring(3)));
                    modified = true;
                } else if (line.startsWith("map_Kd "))
                {
                    currentMaterial.setDiffuseColor(Color.WHITE);
                    currentMaterial.setDiffuseMap(loadImage(line.substring("map_Kd ".length())));
//                    material.setSelfIlluminationMap(loadImage(line.substring("map_Kd ".length())));
//                    material.setSpecularColor(Color.WHITE);
                    modified = true;
                    //            } else if (line.startsWith("illum ")) {
                    //                int illumNo = Integer.parseInt(line.substring("illum ".length()));
                    /*
                     0	 Color on and Ambient off 
                     1	 Color on and Ambient on 
                     2	 Highlight on 
                     3	 Reflection on and Ray trace on 
                     4	 Transparency: Glass on 
                     Reflection: Ray trace on 
                     5	 Reflection: Fresnel on and Ray trace on 
                     6	 Transparency: Refraction on 
                     Reflection: Fresnel off and Ray trace on 
                     7	 Transparency: Refraction on 
                     Reflection: Fresnel on and Ray trace on 
                     8	 Reflection on and Ray trace off 
                     9	 Transparency: Glass on 
                     Reflection: Ray trace off 
                     10	 Casts shadows onto invisible surfaces 
                     */
                } else
                {
//                    log("material line ignored for " + name + ": " + line);
                }
            } catch (Exception ex)
            {
//                Logger.getLogger(MtlReader.class.getName()).log(Level.SEVERE, "Failed to parse line:" + line, ex);
            }
        }
        if (currentMaterial != null)
        {
            addMaterial(name, currentMaterial);
        }
    }

    private void addMaterial(String materialName, PhongMaterial material)
    {
        Optional<PhongMaterial> existingMaterial = materialAlreadyExists(material);
        if (existingMaterial.isPresent())
        {
            Set<String> existingNames = materials.get(existingMaterial.get());
            existingNames.add(materialName);
        } else
        {
            Set<String> newName = new HashSet<>();
            newName.add(materialName);
            materials.put(material, newName);
        }
    }

    private Optional<PhongMaterial> materialAlreadyExists(PhongMaterial materialToCheck)
    {
        Optional<PhongMaterial> existingMaterial = Optional.empty();

        for (Entry<PhongMaterial, Set<String>> materialEntry : materials.entrySet())
        {
            if (materialIsSame(materialToCheck, materialEntry.getKey()))
            {
                existingMaterial = Optional.of(materialEntry.getKey());
                break;
            }
        }

        return existingMaterial;
    }

    private Color readColor(String line)
    {
        String[] split = line.trim().split(" +");
        float red = Float.parseFloat(split[0]);
        float green = Float.parseFloat(split[1]);
        float blue = Float.parseFloat(split[2]);
        return Color.color(red, green, blue);
    }

    private Image loadImage(String filename)
    {
        filename = baseUrl + filename;
//        log("Loading image from " + filename);
        Image image = new Image(filename);
        return new Image(filename);
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> getMaterials()
    {
        Map<String, Integer> materialsAgainstNumbers = new HashMap<>();

        int materialNumber = 0;

        for (Entry<PhongMaterial, Set<String>> materialEntry : materials.entrySet())
        {
            for (String materialName : materialEntry.getValue())
            {
                materialsAgainstNumbers.put(materialName, materialNumber);
            }
            materialNumber++;
        }
        return materialsAgainstNumbers;
    }

    private boolean materialIsSame(PhongMaterial material1, PhongMaterial material2)
    {
        boolean diffuseColourMatch = false;

        if (material1.getDiffuseColor() != null
                && material2.getDiffuseColor() != null)
        {
            diffuseColourMatch = material1.getDiffuseColor().equals(material2.getDiffuseColor());
        }

        return diffuseColourMatch;
    }
}
