package celtech.coreUI.visualisation.collision;

import celtech.modelcontrol.ModelContainer;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class CollisionManager implements CollisionShapeListener
{

    private final Stenographer steno = StenographerFactory.getStenographer(CollisionManager.class.getName());
//    private CollisionWorld collisionWorld = null;
//    private final Map<ModelContainer, GhostObject> monitoredModels = new HashMap<>();
//    private final Map<ModelContainer, Transform> monitoredModelsTransforms = new HashMap<>();
//    private final Map<ModelContainer, Transform> monitoredModelsCentringTransforms = new HashMap<>();
    private final Timer scheduledTickTimer = new Timer(true);
    private final TimerTask tickRun;
    private long lastTickMilliseconds = 0;

    public CollisionManager()
    {
        tickRun = new TimerTask()
        {
            @Override
            public void run()
            {
                collisionTick();
            }
        };

        setupCollisionWorld();

        lastTickMilliseconds = System.currentTimeMillis();
        scheduledTickTimer.scheduleAtFixedRate(tickRun, 0, 250);
    }

    private void setupCollisionWorld()
    {
//        DefaultCollisionConfiguration collisionConfiguration
//                = new DefaultCollisionConfiguration();
//        // calculates exact collision given a list possible colliding pairs
//        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
//
//        // the maximum size of the collision world. Make sure objects stay 
//        // within these boundaries. Don't make the world AABB size too large, it
//        // will harm simulation quality and performance
//        Vector3f worldAabbMin = new Vector3f(-1000, -1000, -1000);
//        Vector3f worldAabbMax = new Vector3f(1000, 1000, 1000);
//        // maximum number of objects
//        final int maxProxies = 1024;
//        // Broadphase computes an conservative approximate list of colliding pairs
//        BroadphaseInterface broadphase = new AxisSweep3(
//                worldAabbMin, worldAabbMax, maxProxies);
//        broadphase.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
//
//        // provides discrete rigid body simulation
//        collisionWorld = new CollisionWorld(dispatcher, broadphase, collisionConfiguration);
    }

    public void addModel(ModelContainer model)
    {
//        if (model.getCollisionShape() != null)
//        {
//            createGhost(model);
//        } else
//        {
////            CollisionShape potentialCollisionShape = model.addCollisionShapeListener(this);
////            if (potentialCollisionShape != null)
////            {
////                createGhost(model);
////            }
//        }
    }

    public void removeModel(ModelContainer model)
    {
        destroyGhost(model);
    }

    private void createGhost(ModelContainer model)
    {
//        GhostObject ghostOfModel = new GhostObject();
////        ghostOfModel.setCollisionShape(model.getCollisionShape());
//        ghostOfModel.setCollisionFlags(CollisionFlags.NO_CONTACT_RESPONSE);
//        collisionWorld.addCollisionObject(ghostOfModel);
//
//        addModelTransformListeners(model);
//
//        monitoredModels.put(model, ghostOfModel);
//        Matrix3f rotation = new Matrix3f();
//        rotation.setIdentity();
//
//        float xOffset = (float) (model.getBoundsInLocal().getMinX() + model.getBoundsInLocal().getWidth() / 2.0);
//        float yOffset = (float) (model.getBoundsInLocal().getMinY() + model.getBoundsInLocal().getHeight() / 2.0);
//        float zOffset = (float) (model.getBoundsInLocal().getMinZ() + model.getBoundsInLocal().getDepth() / 2.0);
//
//        Transform centringTransform = new Transform(rotation);
//        centringTransform.origin.set(xOffset, 0, zOffset);
//        
//        monitoredModelsCentringTransforms.put(model, centringTransform);
//        
//        monitoredModelsTransforms.put(model, centringTransform);
    }

    private void destroyGhost(ModelContainer model)
    {
//        removeAllModelListeners(model);
//
//        if (monitoredModels.get(model) != null)
//        {
//            collisionWorld.removeCollisionObject(monitoredModels.get(model));
//        }
//
//        monitoredModels.remove(model);
//        monitoredModelsTransforms.remove(model);
    }

    @Override
    public void collisionShapeAvailable(ModelContainer model)
    {
        createGhost(model);
    }

    private void addModelTransformListeners(ModelContainer model)
    {
    }

    private void removeModelTransformListeners(ModelContainer model)
    {
    }

    private void removeAllModelListeners(ModelContainer model)
    {
        model.removeCollisionShapeListener(this);
        removeModelTransformListeners(model);
    }

    private void collisionTick()
    {
//        long timeNowMilliseconds = System.currentTimeMillis();
////        steno.info("Tock");
//        collisionWorld.performDiscreteCollisionDetection();
//
//        int numManifolds = collisionWorld.getDispatcher().getNumManifolds();
//
//        Set<CollisionObject> collidedObjects = new HashSet<>();
//
//        for (int i = 0; i < numManifolds; i++)
//        {
//            PersistentManifold contactManifold = collisionWorld.getDispatcher().getManifoldByIndexInternal(i);
//            CollisionObject objA = (CollisionObject) contactManifold.getBody0();
//            CollisionObject objB = (CollisionObject) contactManifold.getBody1();
//
//            int numContacts = contactManifold.getNumContacts();
//            for (int j = 0; j < numContacts; j++)
//            {
//                ManifoldPoint pt = contactManifold.getContactPoint(j);
//                if (pt.getDistance() < 0.f)
//                {
//                    for (Entry<ModelContainer, GhostObject> ghostEntry : monitoredModels.entrySet())
//                    {
//                        if (ghostEntry.getValue() == objA || ghostEntry.getValue() == objB)
//                        {
//                            collidedObjects.add(objA);
//                            collidedObjects.add(objB);
//                        }
//                    }
//                }
//            }
//        }
//
//        for (Entry<ModelContainer, GhostObject> monitoredModelEntry : monitoredModels.entrySet())
//        {
//            monitoredModelEntry.getKey().setCollided(collidedObjects.contains(monitoredModelEntry.getValue()));
//        }
//
//        lastTickMilliseconds = timeNowMilliseconds;
    }

    public void modelsTransformed(Set<ModelContainer> modelContainers)
    {
//        for (ModelContainer model : modelContainers)
//        {
////            Transform worldTxform = new Transform();
////            worldTxform.transform(new Vector3f((float) model.getTransformedCentreX(), (float) model.getTransformedCentreY(), (float) model.getTransformedCentreDepth()));
//            GhostObject ghost = monitoredModels.get(model);
//            if (ghost != null)
//            {
////                monitoredModelsTransforms.get(model).origin.set((float) model.getTransformedCentreX(), (float) model.getTransformedCentreY(), (float) model.getTransformedCentreDepth());
//                Transform centringTransform = monitoredModelsCentringTransforms.get(model);
//                monitoredModelsTransforms.get(model).origin.set(
//                        (float) model.getTransformedCentreX() + centringTransform.origin.x,
//                        0,
//                        (float) model.getTransformedCentreDepth() + centringTransform.origin.z);
////                ghost.
//                ghost.setWorldTransform(monitoredModelsTransforms.get(model));
//            }
//        }
    }
}
