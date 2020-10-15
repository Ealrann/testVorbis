package test.vorbis;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;

public class Main
{
	private static final String FILE_PATH = "fire.ogg";

	public static void main(String[] args)
	{
		final var stack = MemoryStack.stackPush();
		final var channelsBuffer = stack.mallocInt(1);
		final var sampleRateBuffer = stack.mallocInt(1);

		final var encodedData = readFile();
		final var rawAudioBuffer = stb_vorbis_decode_memory(encodedData, channelsBuffer, sampleRateBuffer);

		// Another way with decoder : same result.
		// final var vorbisAlloc = STBVorbisAlloc.mallocStack(stack);
		// final IntBuffer errorBuffer = stack.mallocInt(1);
		// final long decoder = stb_vorbis_open_memory(encodedData, errorBuffer, vorbisAlloc);
		// final int error = stb_vorbis_get_error(decoder);

		if (rawAudioBuffer == null)
		{
			throw new AssertionError("stb_vorbis_decode_memory returned null");
		}
	}

	public static ByteBuffer readFile()
	{
		try (final var inputStream = getInputStream())
		{
			final byte[] byteArray = inputStream.readAllBytes();
			final var buffer = MemoryUtil.memAlloc(byteArray.length);
			buffer.put(byteArray);
			buffer.flip();
			return buffer;
		}
		catch (final Exception e)
		{
			throw new AssertionError("Cannot read file");
		}
	}

	private static InputStream getInputStream()
	{
		try
		{
			final var module = Main.class.getModule();
			return module.getResourceAsStream(FILE_PATH);
		}
		catch (final IOException e)
		{
			throw new AssertionError("Cannot find file");
		}
	}
}
