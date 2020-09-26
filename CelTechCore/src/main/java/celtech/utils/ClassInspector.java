/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ianhudson
 */
public class ClassInspector
{

    /**
     *
     * @param c
     */
    public static void inspect(Class<?> c)
    {

//        try
//        {
            System.out.format("Class:%n  %s%n%n", c.getCanonicalName());
            System.out.format("Modifiers:%n  %s%n%n",
                    Modifier.toString(c.getModifiers()));

            System.out.format("Type Parameters:%n");
            TypeVariable[] tv = c.getTypeParameters();
            if (tv.length != 0)
            {
                System.out.format("  ");
                for (TypeVariable t : tv)
                {
                    System.out.format("%s ", t.getName());
                }
                System.out.format("%n%n");
            } else
            {
                System.out.format("  -- No Type Parameters --%n%n");
            }

            System.out.format("Implemented Interfaces:%n");
            Type[] intfs = c.getGenericInterfaces();
            if (intfs.length != 0)
            {
                for (Type intf : intfs)
                {
                    System.out.format("  %s%n", intf.toString());
                }
                System.out.format("%n");
            } else
            {
                System.out.format("  -- No Implemented Interfaces --%n%n");
            }

            System.out.format("Inheritance Path:%n");
            List<Class> l = new ArrayList<Class>();
            printAncestor(c, l);
            if (l.size() != 0)
            {
                for (Class<?> cl : l)
                {
                    System.out.format("  %s%n", cl.getCanonicalName());
                }
                System.out.format("%n");
            } else
            {
                System.out.format("  -- No Super Classes --%n%n");
            }

            System.out.format("Annotations:%n");
            Annotation[] ann = c.getAnnotations();
            if (ann.length != 0)
            {
                for (Annotation a : ann)
                {
                    System.out.format("  %s%n", a.toString());
                }
                System.out.format("%n");
            } else
            {
                System.out.format("  -- No Annotations --%n%n");
            }

            // production code should handle this exception more gracefully
//        } catch (ClassNotFoundException x)
//        {
//            x.printStackTrace();
//        }
    }

    

    private static void printAncestor(Class<?> c, List<Class> l)
    {
        Class<?> ancestor = c.getSuperclass();
        if (ancestor != null)
        {
            l.add(ancestor);
            printAncestor(ancestor, l);
        }
    }
}
