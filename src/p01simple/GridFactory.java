package p01simple;

import lwjglutils.OGLBuffers;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class GridFactory {

    public static OGLBuffers generateGrid(int a, int b, int topology) {

        switch (topology) {
            case GL_TRIANGLES:
                return generateGridList(a, b);
            default:
               return generateGridTriangleStrip(a, b);
        }
    }

    /**
     * GENERATE triangle list
     *
     * @param a počet vrcholů v řádku
     * @param b počet vrcholů ve sloupci
     * @return
     */
    private static OGLBuffers generateGridList(int a, int b) {
        float[] vb = getVertexArray(a, b);

        int[] ib = new int[(a - 1) * (b - 1) * 2 * 3];
        int indexIB = 0;
        for (int i = 0; i < b - 1; i++) {
            int row = i * a;
            for (int j = 0; j < a - 1; j++) {
                ib[indexIB++] = j + row;
                ib[indexIB++] = j + a + row;
                ib[indexIB++] = j + 1 + row;

                ib[indexIB++] = j + a + row;
                ib[indexIB++] = j + a + 1 + row;
                ib[indexIB++] = j + 1 + row;

            }
        }

        OGLBuffers.Attrib[] attribs = {
                new OGLBuffers.Attrib("inPosition", 2), // 2 floats
        };

        return new OGLBuffers(vb, attribs, ib);
    }

    /**
     * GENERATE triangle strip
     *
     * @param a počet vrcholů v řádku
     * @param b počet vrcholů ve sloupci
     * @return
     */
    private static OGLBuffers generateGridTriangleStrip(int a, int b) {
        float[] vb = getVertexArray(a, b);
        int[] ib = new int[(a * (b - 1))*2 + b - 2];
        int indexIB = 0;
        // pro kazdy vrchol na ose Y
        for (int i = 0; i < b - 1; i++) {
            int row = i * a;

            if (i % 2 == 1) {
                for (int j = row + (a - 1); j >= row; j--) {
                    // pokud dojedu na zacatek radku, tak pridam konecny vrchol
                    if (row > 0 && j == row + (a - 1)) {
                        ib[indexIB++] = j;
                    }
                    ib[indexIB++] = j + a;
                    ib[indexIB++] = j;
                }
            } else {
                for (int j = 0; j < a; j++) {
                    // pokud dojedu na konec radku, tak pridam konecny vrchol
                    if (j == 0 && row > 0) {
                        ib[indexIB++] = j + row;
                    }
                    ib[indexIB++] = j + row;
                    ib[indexIB++] = j + a + row;
                }
            }
        }
        OGLBuffers.Attrib[] attribs = {
                new OGLBuffers.Attrib("inPosition", 2), // 2 floats
        };


        return new OGLBuffers(vb, attribs, ib);
    }


    private static float[] getVertexArray(int a, int b) {
        float[] vb = new float[a * b * 2];
        int indexVB = 0;

        for (int i = 0; i < b; i++) {
            for (int j = 0; j < a; j++) {
                vb[indexVB++] = j / (float) (a - 1);
                vb[indexVB++] = i / (float) (b - 1);
            }
        }
        return vb;
    }

}
