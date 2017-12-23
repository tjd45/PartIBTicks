package gfx.tick1;

import java.awt.image.BufferedImage;

public class Renderer {

    private final Vector3 BACKGROUND_COLOUR = new Vector3(0.1);

    // The width and height of the image in pixels
    private int width, height;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected Vector3 trace(Scene scene, Ray ray) {

        RaycastHit closestHit = scene.findClosestIntersection(ray);

        // If no object has been hit, return a background colour
        Sphere object = closestHit.getObjectHit();
        if (object == null){
            return BACKGROUND_COLOUR;
        }

//        return object.getColour();

        Vector3 P = closestHit.getLocation();
        Vector3 N = closestHit.getNormal();
        return this.illuminate(scene, object, P, N);

    }

    private Vector3 illuminate(Scene scene, Sphere object, Vector3 P, Vector3 N) {

        PointLight light = scene.getPointLight();
        double distanceToLight = light.getPosition().subtract(P).magnitude();

        Vector3 I_a = scene.getAmbientLighting();
        Vector3 I = light.getIlluminationAt(distanceToLight);
    
        Vector3 C_diff = object.getColour();     // Diffuse colour defined by the object
        Vector3 C_spec = new Vector3(1);         // Specular colour is white
        Vector3 camera = new Vector3(0);
        
        double k_d = object.getPhong_kD();
        double k_s = object.getPhong_kS();
        double n = object.getPhong_n();

        Vector3 L = (light.getPosition().subtract(P).normalised());
        Vector3 V = (camera.subtract(P).normalised()); 
        Vector3 R = L.reflectIn(N);
        
        double NdotL = N.dot(L);
        double RdotV = R.dot(V);
        
        Vector3 L_amb = C_diff.scale(I_a);
        Vector3 L_diff = C_diff.scale((k_d*Math.max(NdotL,0))).scale(I);
        Vector3 L_spec = C_spec.scale((k_s)*Math.pow(Math.max(RdotV, 0), n)).scale(I);
        
        return L_spec.add(L_diff).add(L_amb);

    }

    public BufferedImage render(Scene scene) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Camera camera = new Camera(width, height);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Ray ray = camera.castRay(x, y);
                Vector3 pixel = trace(scene, ray);
                image.setRGB(x, y, pixel.toRGB());
            }
            System.out.println(String.format("%.2f", 100*y/(float)(height-1))+"% completed");
        }

        return image;
    }
}