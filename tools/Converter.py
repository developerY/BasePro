import subprocess
from moviepy.editor import VideoFileClip

input_path = '/content/in_video.mov'
output_path = '/content/final_output.gif'

# --- Customization ---
start_time = 2
end_time = 29.7
new_width = 480

clip = (VideoFileClip(input_path)
        .subclip(start_time, end_time)
        .speedx(2)
        .resize(width=new_width))

# --- Use FFMPEG for High-Quality GIF Conversion ---
temp_video_path = "/content/temp_video.mp4"
print("Processing initial video clip...")
# Use progress_bar=True instead of logger=None
clip.write_videofile(temp_video_path, codec="libx264", progress_bar=True)

# --- Define the filters ---
filters = f"fps=15,scale={new_width}:-1:flags=lanczos,zscale=t=linear:npl=100,format=gbrpf32le,zscale=p=bt709,tonemap=tonemap=hable:desat=0,zscale=t=bt709:m=bt709:r=tv,format=yuv420p"

# Step 1: Generate the palette
palette_path = "/content/palette.png"
print("Step 1/2: Generating high-quality color palette...")
subprocess.run(
    f"ffmpeg -i {temp_video_path} -vf '{filters},palettegen' -y {palette_path}",
    shell=True,
    stdout=subprocess.DEVNULL, # Hide ffmpeg's stdout
    stderr=subprocess.DEVNULL  # Hide ffmpeg's stderr
)

# Step 2: Create the final GIF
print("Step 2/2: Assembling final GIF with new palette...")
subprocess.run(
    f"ffmpeg -i {temp_video_path} -i {palette_path} -filter_complex '[0:v]{filters}[x];[x][1:v]paletteuse' -y {output_path}",
    shell=True,
    stdout=subprocess.DEVNULL, # Hide ffmpeg's stdout
    stderr=subprocess.DEVNULL  # Hide ffmpeg's stderr
)

print(f"\n✅ Successfully created final GIF: {output_path}")