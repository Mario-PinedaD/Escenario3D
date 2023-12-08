package escenario3d;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.GL;
import static com.jogamp.opengl.GL.*;  // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;

// Clases necesarias para el uso de texturas
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.jogamp.opengl.GLProfile;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
//-Djava.library.path=C:\Users\amoel\Downloads\joamp\windows-amd64

/**
 *
 * @author Mario Pineda
 */
@SuppressWarnings("serial")
public class Escenario3D extends GLJPanel implements GLEventListener, KeyListener {

  private static String TITLE = "Casa en Minecraft de madera";
  private static final int CANVAS_WIDTH = 1500;  // width of the drawable
  private static final int CANVAS_HEIGHT = 844; // height of the drawable
  private static final int FPS = 60; // animator's target frames per second
  private static final float factInc = 5.0f; // animator's target frames per second
  float fovy = 45.0f;
  int eje = 0;
  float rotX = 0.0f;
  float rotY = 0.0f;
  float rotZ = 0.0f;

  float posCamX = 0.0f;
  float posCamY = 0.0f;
  float posCamZ = 0.0f;

  float distanciaCam = 25.0f;

  Texture textura1;
  Texture textura2;
  Texture textura3;
  Texture textura4;
  Texture textura5;
  Texture textura6;
  Texture textura7;
  Texture textura8;

  public static void main(String[] args) {
    // Run the GUI codes in the event-dispatching thread for thread safety
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        // Create the OpenGL rendering canvas
        GLJPanel canvas = new Escenario3D();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // Create a animator that drives canvas' display() at the specified FPS.
        final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

        // Create the top-level container
        final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame

        BorderLayout fl = new BorderLayout();
        frame.setLayout(fl);

        frame.getContentPane().add(canvas, BorderLayout.CENTER);

        frame.addKeyListener((KeyListener) canvas);
        frame.addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            // Use a dedicate thread to run the stop() to ensure that the
            // animator stops before program exits.
            new Thread() {
              @Override
              public void run() {
                if (animator.isStarted()) {
                  animator.stop();
                }
                System.exit(0);
              }
            }.start();
          }
        });

        frame.setTitle(TITLE);
        frame.pack();
        frame.setVisible(true);
        animator.start(); // start the animation loop
      }
    });
  }

  Texture cargarTextura(String imageFile) {
    Texture text1 = null;
    try {
      BufferedImage buffImage = ImageIO.read(new File(imageFile));
      text1 = AWTTextureIO.newTexture(GLProfile.getDefault(), buffImage, false);
    } catch (IOException ioe) {
    }
    return text1;
  }

  private GLU glu;  // for the GL Utility
  private GLUT glut;

  /**
   * Constructor to setup the GUI for this Component
   */
  public Escenario3D() {
    this.addGLEventListener(this);
    this.addKeyListener(this);
  }

  //color del fondo
  float rojo = 0.5f;
  float verde = 0.8f;
  float azul = 1.0f;

  // Posicion de la luz.
  float lightX = 1f;
  float lightY = 1f;
  float lightZ = 1f;
  float dLight = 0.05f;
  final float ambient[] = {0.1f, 0.1f, 0.1f, 0.1f};
  final float position[] = {lightX, lightY, lightZ, 1.0f};
  final float[] colorWhite = {1.0f, 1.0f, 1.0f, 1.0f};

  float intensidadLuz = 0.0f;

  @Override
  public void init(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    glu = new GLU();
    glut = new GLUT();
    gl.glClearColor(rojo, verde, azul, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL_DEPTH_TEST);
    gl.glDepthFunc(GL_LEQUAL);
    gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

    float[] whiteMaterial = {1.0f, 1.0f, 1.0f};
    gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, whiteMaterial, 0);
    gl.glShadeModel(GL_SMOOTH);

    // Configuración de la luz
    float[] ambientLight = {intensidadLuz, intensidadLuz, intensidadLuz, 0.0f};
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0);
    float[] diffuseLight = {intensidadLuz, intensidadLuz, intensidadLuz, 0f};
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);
    float[] specularLight = {1f, 1f, 1f, 0f};
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularLight, 0);

    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);

    //Lamparas
    float[] emissionLight = {1.0f, 1.0f, 1.0f, 1.0f};
    gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, emissionLight, 0);
    float[] emissionLightColor = {1.0f, 1.0f, 1.0f, 1.0f};
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_EMISSION, emissionLightColor, 0);

    gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting  
    gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, this.ambient, 0);
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, colorWhite, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, colorWhite, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);

    this.initPosition(gl);

    this.textura1 = this.cargarTextura("texturas/pasto.jpg");
    this.textura2 = this.cargarTextura("texturas/pared.jpg");
    this.textura3 = this.cargarTextura("texturas/puerta.jpg");
    this.textura4 = this.cargarTextura("texturas/techo.jpg");
    this.textura5 = this.cargarTextura("texturas/tierra.jpg");
    this.textura6 = this.cargarTextura("texturas/tronco.jpg");
    this.textura7 = this.cargarTextura("texturas/hojas.jpg");
    this.textura8 = this.cargarTextura("texturas/ventana.jpg");

    gl.glEnable(GL2.GL_TEXTURE_2D);
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
  }

/////////////// Move light ////////////////////////////
  public void moveLightX(boolean positivDirection) {
    lightX += positivDirection ? dLight : -dLight;
  }

  public void moveLightY(boolean positivDirection) {
    lightY += positivDirection ? dLight : -dLight;
  }

  public void moveLightZ(boolean positivDirection) {
    lightZ += positivDirection ? dLight : -dLight;
  }

  public void initPosition(GL2 gl) {
    float posLight1[] = {lightX, lightY, lightZ, 1.0f}; //La posicion de la luz se va movimendo aqui chido
    float spotDirection1[] = {0.0f, -1.f, 0.f};
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posLight1, 0);
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

    if (height == 0) {
      height = 1;   // prevent divide by zero
    }
    float aspect = (float) width / height;

    // Set the view port (display area) to cover the entire window
    gl.glViewport(0, 0, width, height);

    // Setup perspective projection, with aspect ratio matches viewport
    gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
    gl.glLoadIdentity();             // reset projection matrix
    glu.gluPerspective(fovy, aspect, 0.1, 50.0); // fovy, aspect, zNear, zFar

    // Enable the model-view transform
    gl.glMatrixMode(GL_MODELVIEW);
    gl.glLoadIdentity(); // reset
  }

  public void animate(GL2 gl) {
    float posLight0[] = {lightX, lightY, lightZ, 1.f};
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posLight0, 0);
    //drawGlow(gl);
    //lightX += 0.003f;
    //lightY += 0.003f;
  }

  @Override
  public void display(GLAutoDrawable drawable) {

    float aspect = (float) this.getWidth() / this.getHeight();
    GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers

    float[] lightPos = {0.0f, 5.0f, 10.0f, 1};
    //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

    gl.glLoadIdentity();  // reset the model-view matrix
    gl.glEnable(GL.GL_LINE_SMOOTH);
    gl.glEnable(GL.GL_BLEND);

    gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);

    glu.gluLookAt(0.0, 0.0, distanciaCam, this.posCamX, this.posCamY, this.posCamZ, 0.0, 1.0, 0.0);

    if (rotX < 0) {
      rotX = 360 - factInc;
    }
    if (rotY < 0) {
      rotY = 360 - factInc;
    }
    if (rotZ < 0) {
      rotZ = 360 - factInc;
    }

    if (rotX >= 360) {
      rotX = 0;
    }
    if (rotY >= 360) {
      rotY = 0;
    }
    if (rotZ >= 360) {
      rotZ = 0;
    }

    gl.glRotatef(rotX, 1.0f, 0.0f, 0.0f);
    gl.glRotatef(rotY, 0.0f, 1.0f, 0.0f);
    gl.glRotatef(rotZ, 0.0f, 0.0f, 1.0f);

    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    /*float no_mat[] = {0.0f, 0.0f, 0.0f, 1.0f};
        float mat_ambient[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float mat_ambient_color[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float mat_diffuse[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float no_shininess[] = {0.0f};
        float low_shininess[] = {5.0f};
        float high_shininess[] = {100.0f};
        float mat_emission[] = {0.5f, 0.5f, 0.5f, 0.0f};

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, high_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, mat_emission, 0);*/
    // Asociar la textura con el canvas
    this.textura1.bind(gl);
    this.textura1.enable(gl);

    // glut.glutSolidTeapot(1);
    this.drawFloor(gl);
    //this.drawCubeUVWmapped(gl);

    this.textura1.disable(gl);

    // Asociar la textura con el canvas
    this.textura5.bind(gl);
    this.textura5.enable(gl);

    this.drawDirt(gl);

    this.textura5.disable(gl);

    /*// Configurar material y luz para el objeto que emite luz
        float[] glowMaterial = {1.0f, 1.0f, 1.0f, 1.0f}; // Ajusta emissionR, emissionG, emissionB según tu necesidad
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, glowMaterial, 0);
        this.textura4.bind(gl);
        this.textura4.enable(gl);
        this.animate(gl);
        this.textura4.disable(gl);
        
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, high_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, mat_emission, 0);
        
        // Después de restablecer la emisión a su estado original
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, high_shininess, 0);*/
    gl.glPushMatrix();
    gl.glTranslatef(0.0f, 1.2f, 0.0f);
    this.textura2.bind(gl);
    this.textura2.enable(gl);
    gl.glPushMatrix();
    gl.glTranslatef(0.0f, 0.0f, 5.0f);
    gl.glScalef(7.0f, 3.0f, 1.0f);
    this.drawWall(gl);
    gl.glTranslatef(0.0f, 0.0f, -10.0f);
    this.textura8.bind(gl);
    this.textura8.enable(gl);
    this.drawWall(gl);
    this.textura8.disable(gl);
    this.textura2.bind(gl);
    this.textura2.enable(gl);
    gl.glPopMatrix();
    gl.glPushMatrix();
    gl.glTranslatef(6.0f, 0.0f, 0.0f);
    gl.glScalef(1.0f, 3.0f, 4.8f);
    this.drawWall(gl);
    gl.glPopMatrix();
    this.textura2.disable(gl);

    this.textura3.bind(gl);
    this.textura3.enable(gl);
    gl.glPushMatrix();
    gl.glTranslatef(-6.0f, 0.0f, 0.0f);
    gl.glScalef(1.0f, 3.0f, 4.8f);
    this.drawWall(gl);
    gl.glPopMatrix();
    this.textura3.disable(gl);
    gl.glPopMatrix();

    this.textura4.bind(gl);
    this.textura4.enable(gl);
    gl.glPushMatrix();
    gl.glTranslatef(0.0f, -0.6f, 0.0f);
    gl.glScalef(2.0f, 1.5f, 1.8f);
    this.drawRoof(gl);
    gl.glPopMatrix();
    this.textura4.disable(gl);

    gl.glPushMatrix();
    gl.glTranslatef(-10.0f, 0.0f, 10.0f);
    this.drawTree(gl);
    gl.glPopMatrix();

    gl.glFlush();

    this.rotY += 0.5f;
  }

  void drawTree(GL2 gl) {
    this.textura6.bind(gl);
    this.textura6.enable(gl);
    gl.glPushMatrix();
    gl.glTranslatef(0.0f, 0.7f, 0.0f);
    gl.glScalef(0.6f, 2.5f, 0.6f);
    this.drawWall(gl);
    gl.glPopMatrix();
    this.textura6.disable(gl);
    this.textura7.bind(gl);
    this.textura7.enable(gl);
    this.drawRoof(gl);
    this.textura7.disable(gl);

  }

  void drawFloor(GL2 gl) {
    gl.glPushMatrix();
    gl.glScalef(15.0f, 0.3f, 15.0f);
    gl.glTranslatef(0f, -7.0f, 0f);

    gl.glBegin(GL2.GL_QUADS);
    // Front Face

    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, 1.0f, 1.0f);	// Bottom Left
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Top Left
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Top Right
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Bottom Right

    // Back Face
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Right
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(-1.0f, 1.0f, -1.0f);	// Top Right
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(1.0f, 1.0f, -1.0f);	// Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, -1.0f);	// Bottom Left

    // Top Face
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(-1.0f, 1.0f, -1.0f);	// Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, 1.0f, 1.0f);	// Bottom Left
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Bottom Right
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(1.0f, 1.0f, -1.0f);	// Top Right

    // Bottom Face
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Top Right
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(1.0f, -1.0f, -1.0f);	// Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Bottom Left
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Bottom Right
    // Right face
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, -1.0f);	// Bottom Right
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(1.0f, 1.0f, -1.0f);	// Top Right
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Bottom Left
    // Left Face
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Left
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Bottom Right
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(-1.0f, 1.0f, 1.0f);	// Top Right
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(-1.0f, 1.0f, -1.0f);	// Top Left

    gl.glEnd();

    gl.glPopMatrix();

  }

  void drawDirt(GL2 gl) {
    gl.glPushMatrix();
    gl.glScalef(15.0f, 0.8f, 15.0f);
    gl.glTranslatef(0f, -4.0f, 0f);

    // Configura el modo de repetición de la textura
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

    gl.glBegin(GL2.GL_QUADS);

    // Front Face
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Bottom Left
    gl.glTexCoord2f(0.0f, 2.0f);
    gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Top Left
    gl.glTexCoord2f(2.0f, 2.0f);
    gl.glVertex3f(1.0f, -1.0f, 1.0f); // Top Right
    gl.glTexCoord2f(2.0f, 0.0f);
    gl.glVertex3f(1.0f, 1.0f, 1.0f); // Bottom Right

    // Back Face
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Right
    gl.glTexCoord2f(0.0f, 2.0f);
    gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Right
    gl.glTexCoord2f(2.0f, 2.0f);
    gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Left
    gl.glTexCoord2f(2.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Left

    // Top Face
    gl.glTexCoord2f(0.0f, 2.0f);
    gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Bottom Left
    gl.glTexCoord2f(2.0f, 0.0f);
    gl.glVertex3f(1.0f, 1.0f, 1.0f); // Bottom Right
    gl.glTexCoord2f(2.0f, 2.0f);
    gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right

    // Bottom Face
    gl.glTexCoord2f(2.0f, 2.0f);
    gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Top Right
    gl.glTexCoord2f(0.0f, 2.0f);
    gl.glVertex3f(1.0f, -1.0f, -1.0f); // Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left
    gl.glTexCoord2f(2.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Right

    // Right Face
    gl.glTexCoord2f(2.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right
    gl.glTexCoord2f(2.0f, 2.0f);
    gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right
    gl.glTexCoord2f(0.0f, 2.0f);
    gl.glVertex3f(1.0f, 1.0f, 1.0f); // Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left

    // Left Face
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left
    gl.glTexCoord2f(2.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Right
    gl.glTexCoord2f(2.0f, 2.0f);
    gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Right
    gl.glTexCoord2f(0.0f, 2.0f);
    gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left

    gl.glEnd();

    gl.glPopMatrix();

  }

  public static void drawWall(GL2 gl) {

    gl.glPushMatrix();

    gl.glScalef(1.0f, 1.0f, 1.0f);
    gl.glTranslatef(0.0f, 0.0f, 0.0f);
    gl.glBegin(GL2.GL_QUADS);
    // Front Face

    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, 1.0f, 1.0f);	// Bottom Left
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Top Left
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Top Right
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Bottom Right

    // Back Face
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Right
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(-1.0f, 1.0f, -1.0f);	// Top Right
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(1.0f, 1.0f, -1.0f);	// Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, -1.0f);	// Bottom Left

    // Top Face
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(-1.0f, 1.0f, -1.0f);	// Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, 1.0f, 1.0f);	// Bottom Left
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Bottom Right
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(1.0f, 1.0f, -1.0f);	// Top Right

    // Bottom Face
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Top Right
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(1.0f, -1.0f, -1.0f);	// Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Bottom Left
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Bottom Right
    // Right face
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, -1.0f);	// Bottom Right
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(1.0f, 1.0f, -1.0f);	// Top Right
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Top Left
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Bottom Left
    // Left Face
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Left
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Bottom Right
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3f(-1.0f, 1.0f, 1.0f);	// Top Right
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3f(-1.0f, 1.0f, -1.0f);	// Top Left

    gl.glEnd();

    gl.glPopMatrix();

  }

  public static void drawRoof(GL2 gl) {
    float scaleX = 3.0f;
    float scaleY = 0.5f;
    float scaleZ = 3.0f;

    float transY = 3.7f;

    for (int i = 0; i < 3; i++) {

      gl.glPushMatrix();

      gl.glTranslatef(0.0f, transY, 0.0f);
      gl.glScalef(scaleX, scaleY, scaleZ);
      gl.glBegin(GL2.GL_QUADS);
      // Front Face

      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(-1.0f, 1.0f, 1.0f);	// Bottom Left
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Top Left
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Top Right
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Bottom Right

      // Back Face
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Right
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(-1.0f, 1.0f, -1.0f);	// Top Right
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(1.0f, 1.0f, -1.0f);	// Top Left
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(1.0f, -1.0f, -1.0f);	// Bottom Left

      // Top Face
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(-1.0f, 1.0f, -1.0f);	// Top Left
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(-1.0f, 1.0f, 1.0f);	// Bottom Left
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Bottom Right
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(1.0f, 1.0f, -1.0f);	// Top Right

      // Bottom Face
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Top Right
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(1.0f, -1.0f, -1.0f);	// Top Left
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Bottom Left
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Bottom Right
      // Right face
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(1.0f, -1.0f, -1.0f);	// Bottom Right
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(1.0f, 1.0f, -1.0f);	// Top Right
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Top Left
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Bottom Left
      // Left Face
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Left
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Bottom Right
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(-1.0f, 1.0f, 1.0f);	// Top Right
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(-1.0f, 1.0f, -1.0f);	// Top Left

      gl.glEnd();

      transY += 1;
      scaleX /= 2;
      scaleZ /= 2;

      gl.glPopMatrix();
    }

  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int cod = e.getKeyCode();
    switch (cod) {
      case KeyEvent.VK_NUMPAD2:
        this.moveLightY(false);
        break;
      case KeyEvent.VK_NUMPAD8:
        this.moveLightY(true);
        break;
      case KeyEvent.VK_NUMPAD6:
        this.moveLightX(true);
        break;
      case KeyEvent.VK_NUMPAD4:
        this.moveLightX(false);
        break;
      case KeyEvent.VK_PAGE_UP:
        this.moveLightZ(false);
        break;
      case KeyEvent.VK_PAGE_DOWN:
        this.moveLightZ(true);
        break;
      case KeyEvent.VK_UP:
        posCamY += 0.5f;
        break;
      case KeyEvent.VK_DOWN:
        posCamY -= 0.5f;
        break;
      case KeyEvent.VK_LEFT:
        posCamX -= 0.5f;
        break;
      case KeyEvent.VK_RIGHT:
        posCamX += 0.5f;
        break;
      case KeyEvent.VK_F1:
        fovy += factInc;
        break;
      case KeyEvent.VK_F2:
        fovy -= factInc;
        break;
      case KeyEvent.VK_3:
        switch (eje) {
          case 1:
            rotX += factInc;
            break;
          case 2:
            rotY += factInc;
            break;
          case 3:
            rotZ += factInc;
            break;
        }
        break;
      case KeyEvent.VK_4:
        switch (eje) {
          case 1:
            rotX -= factInc;
            break;
          case 2:
            rotY -= factInc;
            break;
          case 3:
            rotZ -= factInc;
            break;
        }
        break;

      case KeyEvent.VK_X:
        eje = 1;
        break;
      case KeyEvent.VK_Y:
        eje = 2;
        break;
      case KeyEvent.VK_Z:
        eje = 3;
        break;
      case KeyEvent.VK_S:
        posCamZ -= 0.5f;
        break;
      case KeyEvent.VK_W:
        posCamZ += 0.5f;
        break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
