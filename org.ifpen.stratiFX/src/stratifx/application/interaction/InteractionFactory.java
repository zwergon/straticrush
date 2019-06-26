/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction;

import fr.ifp.kronosflow.utils.LOGGER;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import stratifx.application.interaction.deform.*;
import stratifx.application.interaction.edit.MovePointsInteraction;
import stratifx.application.interaction.tools.*;
import stratifx.application.plot.GFXScene;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GInteraction;

/**
 *
 * @author lecomtje
 */
public class InteractionFactory {

    Map<String, Class<?>> interactionTypes;

    private static InteractionFactory factory = null;

    static {
        factory = new InteractionFactory();
    }

    /**
     * @return the {@link DeformationFactory} singleton instance
     */
    public static InteractionFactory getInstance() {
        return factory;
    }

    /**
     * registers in the Factory instance a class for a {@link GInteraction}
     *
     * @param key the key for the class to be created
     * @param clazz the class of {@link GInteraction} to be created
     */
    public void register(String key, Class<?> clazz) {
        interactionTypes.put(key, clazz);
    }

    private InteractionFactory() {
        
        interactionTypes = new HashMap<>();
        register("Top", TopBorderInteraction.class);
        register("NodeMove", NodeMoveInteraction.class);
        register("Global", GlobalMoveInteraction.class);
        register("Reset", ResetGeometryInteraction.class);
        register("PatchDisplacements", PatchDisplacementsInteraction.class);
        register("MasterSlave", MasterSlaveInteraction.class);
        register("HorizonMS", HorizonMSInteraction.class);
        register("RemoveUnit", RemoveUnitInteraction.class);
        register("Dilatation", DilatationInteraction.class);
        register("Triangulation", TriangulateInteraction.class);
        register("StratiGrid", StratiGridInteraction.class);
        register("Compact2D", Compact2DInteraction.class);
        register("TimeLine", TimeLineInteraction.class);
        register("AntiGravity", AntiGravityInteraction.class);
        register("MovePoints", MovePointsInteraction.class);
        register("BL2DMesh", BL2DMeshInteraction.class);
    }
    
    
     public SectionInteraction createInteraction(String deformationType, GFXScene gScene ) {

        SectionInteraction interaction = null;
        try {
            if (!interactionTypes.containsKey(deformationType)) {
                return null;
            }

            Class<?> c1 = interactionTypes.get(deformationType);

            Constructor constructor =  c1.getConstructor(GScene.class);
            
            interaction = (SectionInteraction)constructor.newInstance(gScene);
        }
        catch (Exception ex) {
            LOGGER.error(ex, getClass());
        }

        return interaction;
    }

}
