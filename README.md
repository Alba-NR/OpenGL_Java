# :milky_way: OpenGL renderer in Java
A scene renderer (rasterizer) written from scratch in Java using the [Light-Weight Java Games Library (LWJGL)](https://www.lwjgl.org/) to access the [OpenGL graphics API](https://www.opengl.org/). I decided to start this personal project to better understand how an OpenGL application works and to expand my graphics programming knowledge through hands-on experience. 

It started by creating a simple OpenGL application to draw a triangle on the screen (trust me- I was very excited to see it render!!), and has evolved to a more powerful renderer capable of drawing a scene with different meshes, applying lighting models and post-processing effects amongst others!

Contents of `README`:
  - :clipboard: [Project Description](#clipboard-project-description)
      - :beginner: [Some details & context](#beginner-some-details-and-context)
      - :checkered_flag: [Features already implemented](#checkered_flag-features-already-implemented)
      - :construction: [Currently working on](#construction-currently-working-on)
      - :triangular_flag_on_post: [(Ideas) New features to implement](#triangular_flag_on_post-ideas-new-features-to-implement)
  - :camera: [Some Screenshots- yay!](#camera-some-screenshots) -- the fun part!! :sunglasses:
  
![Image: Sample Scene Rendered](screenshot_samples/1.jpg "Sample Scene Rendered (15/10/2020)")

**:pushpin: Note:** This repository is set to public to showcase this project; however, this doesn't mean the code can be freely copied and used, please see the [Copyright Notice](#grey_exclamation-copyright-notice) below.

---

## :clipboard: Project Description

### :beginner: Some details and context:

#### Motivation:
After being introduced to OpenGL and it's rendering pipeline during an university course last year (Oct. 2019), during the summer (Aug. 2020) I decided to delve further into the details of how an OpenGL application works by building one from scratch! This way I aimed to explore in more detail the concepts that were mentioned during the lectures. To do this, I used a combination of on-line resources, including the great explanations from [https://learnopengl.com/](https://learnopengl.com/) (OpenGL & C++) and [ThinMatrix's playlist](https://www.youtube.com/watch?v=VS8wlS9hF8E&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP) (Java LWJGL & OpenGL).

#### Project structure:
<img src="screenshot_samples/packages.png" alt="Project Structure" width="15%" align="right">

- The application uses 2 libraries: Light-Weight Java Games Library (LWJGL) and Java OpenGL Math Library (JOML). Their files are in the `lib` folder.
- The main application is included in the `main` package, with `Main`being the program that runs the application defined in `OpenGLApp`.
- The graphics framework is inside the `graphics` package, which has subpackages for each of the different parts of the framework.
- Any resources such as textures, models and shaders are in the appropriate folders in the `resources` folder.

#### Running the application:
When running the main program, a window where the rendering will take place appears. I have implemented the following controls:
- `Move your mouse` to look around the scene, and `scroll` to zoom.
- Use the `AWSD` keys to move left, forwards, backwards and right respectively. 
- Use the `left shift` and `left ctrl` keys to move up or down respectively.
- Press `F` to toggle (turn ON/OFF) the flashlight.
- Press `E` tp view the scene in wireframe mode.
- Press the numbers `0-5` to select the corresponding post-processing effect.

---

### :checkered_flag: Features already implemented:
Here is a list + short explanation of the features I have already implemented (not in the same order as they weere implemented):
- **Scene Graph** -- a scene to render is organised/stored in a graph. Each node can be either a drawable or abstract entity, which has a shape (with a mesh and material) or not respectively. This graph is traversed to render the entities in the scene.
- **Render Engine** -- allows to create the appropriate renderers (Renderer objects) for rendering different parts of the scene (either to the screen or to e.g. a custom FBO). A renderer uses the given ShaderProgram shader to render the scene.
- **Camera** -- the scene is rendered and can be seen as if from a 1st person view. You can move through the scene, look around and zoom in/out by using certain keyboard keys and a mouse (see 'how to use' section :wink:).
- **Shaders** -- loads and links together .glsl files for vertex, fragment and geometry shaders, creating a shader program OpenGL object that will be used for rendering.
- **Rendering Meshes** -- render meshes whose vertex, normals and texture coordinates are specified explicitly in their class.
- **Loading Models** -- parse an .OBJ file to extract the mesh data of a model, to use it in the application. Done using `assimp` (note: models made up of a single mesh).
- **Loading Images** -- parse image files in RGB or RGBA (e.g. jpg, png). Used for creating OpenGL textures to use when rendering.
- Objects have **materials** (to use with the **Phong or Blinn-Phong illumination models**):
  - Material -- allows you to use plain colours or **textures** for the diffuse & specular components.
  - **Reflective Material** -- a material that has full or partial reflection of the scene's skybox.
  - **Refractive Material** -- a material that has full or partial refraction of the scene's skybox.
- **Skybox** -- load the skybox's faces from 6 images and create an OpenGL cubemap object. Also render it using the appropriate renderer.
- **Lights** -- a scene can have a **directional light**, several **point lights** and a **flashlight** coming from the camera.
- **Shadows** -- uses **shadow mapping** to get the shadows for a scene's directional light and for a point light (later one is still in progress).
- **Post-processing Effects** -- can apply different post-processing effects to the rendered image. Done by rendering scene to a texture, then rendering a quad whose size is that of the screen and which uses the texture created; post-processing effects applied to this texture (may or may not use kernels to manipulate/process the image). e.g. inverting colours, greyscale, blur, sharpenning, edge detection...


### :construction: Currently working on:
- Correctly implementing omnidirectional shadow mapping for 1 point light. 🠪  :warning: **issue:** latest attempt (which generates and renders the shadows- yay!- makes reflection and refraction of the skybox not work.)
- Next: omnidirectional shadow mapping for several point lights.

### :triangular_flag_on_post: (Ideas) New features to implement:
- Normal mapping
- Parallax mapping
- Bloom
- Ambient Occlusion
- Physically Based Rendering (PBR)

---

## :camera: Some Screenshots!!
So this is the fun part of this readme- it's time to see some screenshots of the images rendered!! These are the results, enjoy! :)

*Simple scene with lighting and shadows too:* <br>
<img src="screenshot_samples/2.jpg" alt="Simple scene: lighting & shadows too." width="45%">
<img src="screenshot_samples/3.jpg" alt="Simple scene: lighting & shadows too." width="45%">
<img src="screenshot_samples/4.jpg" alt="Simple scene with skybox, lights and shadows." width="45%">

*(Full) Skybox reflection:* <br>
<img src="screenshot_samples/10.png" alt="Skybox reflection." width="45%"> 
<img src="screenshot_samples/11.png" alt="Skybox reflection." width="45%">

*(Full & Partial) Skybox refraction:* <br>
<img src="screenshot_samples/16.png" alt="Skybox refraction." width="45%">
<img src="screenshot_samples/17.png" alt="Partial skybox refraction." width="45%">

*(Partial) Skybox reflection:* <br>
<img src="screenshot_samples/12.png" alt="Partial skybox reflection." width="45%">
<img src="screenshot_samples/13.png" alt="Skybox reflection." width="45%">
<img src="screenshot_samples/14.png" alt="Skybox reflection." width="45%">

*More scenes:* <br>
<img src="screenshot_samples/9.png" alt="Simple scene with skybox. No shadows." width="45%">
<img src="screenshot_samples/8.png" alt="Dragon. Plain colour material." width="45%"> 
<img src="screenshot_samples/6.png" alt="Flashlight." width="45%">

*Post-processing effects: 0. No effect, 1. Inverted colours, 2. Greyscale, 3. Sharpen, 4. Blur and 5. Edge detection.* <br>
<img src="screenshot_samples/effect0_normal.jpg" alt="No effect." width="45%">
<img src="screenshot_samples/effect1_inverted.jpg" alt="Inverted colours." width="45%">
<img src="screenshot_samples/effect2_greyscale.jpg" alt="Greyscale." width="45%">
<img src="screenshot_samples/effect3_sharpen.jpg" alt="Sharpen." width="45%">
<img src="screenshot_samples/effect4_blur.jpg" alt="Blur." width="45%">
<img src="screenshot_samples/effect5_edges.jpg" alt="Edge detection." width="45%">

---

## :grey_exclamation: Copyright Notice

Copyright &copy; 2020 Alba Navarro Rosales. All rights reserved. Please do not copy or modify the design or software in this repository for any purpose other than with the express written permission of the author, neither claim it as your own. Do check [this](https://choosealicense.com/no-permission/) out, thanks! :) 
<br>:point_up: And remember- plagiarism is bad!
