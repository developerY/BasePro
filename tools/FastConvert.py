from moviepy.editor import VideoFileClip

# --- Main Settings ---
input_path = '/content/in_video.mov'
output_path = '/content/output_fast.gif'

# --- Customization ---
start_time = 2
end_time = 30
new_width = 480
gif_fps = 5

# --- Conversion Process ---
clip = (VideoFileClip(input_path)
        .subclip(start_time, end_time))
#.speedx(2)  # ⏩ Double the playback speed
#.resize(width=new_width))

# Write the final GIF
clip.write_gif(output_path, fps=gif_fps)

print(f"✅ Successfully created fast GIF: {output_path}")

"""
Code in colab
https://colab.research.google.com/drive/1TxRoOKX6PSTFnD5G3XpoAjAqY_rI_qC3?usp=sharing
"""