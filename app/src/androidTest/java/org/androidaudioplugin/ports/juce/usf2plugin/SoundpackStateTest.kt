package org.androidaudioplugin.ports.juce.usf2plugin

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.androidaudioplugin.AudioPluginServiceHelper
import org.androidaudioplugin.hosting.AudioPluginClientBase
import org.androidaudioplugin.hosting.NativeRemotePluginInstance
import org.androidaudioplugin.soundpack.SoundpackKind
import org.androidaudioplugin.soundpack.client.SoundpackClient
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** An SF2 selected through the soundpack libraries is loaded by its identity and comes back
 * from the plugin state, also in a fresh instance. Needs the sample SF2 pack of
 * soundpack-provider-for-android installed (`:samples:sample-sf2-pack:installDebug`). */
class SoundpackStateTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    /** JUCE's copyXmlToBinary(): magic, length, then the XML text with a NUL. */
    private fun juceState(xml: String): ByteArray {
        val text = xml.toByteArray(Charsets.UTF_8)
        return ByteBuffer.allocate(8 + text.size + 1).order(ByteOrder.LITTLE_ENDIAN)
            .putInt(0x21324356).putInt(text.size + 1).put(text).put(0).array()
    }

    private fun NativeRemotePluginInstance.savedSoundFont(): String {
        val state = ByteArray(getStateSize())
        getState(state)
        val xml = String(state, 8, state.size - 8, Charsets.UTF_8)
        return Regex("sf2Absolute=\"([^\"]*)\"").find(xml)?.groupValues?.get(1).orEmpty().replace("&amp;", "&")
    }

    /** Loading is asynchronous: wait until the state names [identity]. */
    private fun NativeRemotePluginInstance.awaitSoundFont(identity: String): String {
        val deadline = System.currentTimeMillis() + 15_000
        var saved = savedSoundFont()
        while (saved != identity && System.currentTimeMillis() < deadline) {
            Thread.sleep(100)
            process(256, 0)
            saved = savedSoundFont()
        }
        return saved
    }

    @Test
    fun soundpackIdentityLoadsAndRoundTripsThroughState() {
        val client = SoundpackClient(context, SoundpackKind.SF2)
        val pack = client.catalog().packs.single { it.id == "org.androidaudioplugin.soundpack.sample.sines" }
        val identity = client.identityOf(client.entries(pack).single { it.path == "Sines.sf2" }).format()

        val pluginInfo = AudioPluginServiceHelper.getLocalAudioPluginService(context).plugins.first()
        val host = AudioPluginClientBase(context)
        runBlocking { host.connectToPluginService(pluginInfo.packageName) }
        try {
            val source = host.instantiateNativePlugin(pluginInfo)
            source.prepare(256, 48000, 0x10000)
            source.activate()
            source.setState(juceState("""<USF2PLUGIN version="2" sf2Bundled="0" sf2Relative="Sines.sf2" sf2Absolute="${identity.replace("&", "&amp;")}"/>"""))
            assertEquals("the plugin loads the identity and saves it", identity, source.awaitSoundFont(identity))
            val saved = ByteArray(source.getStateSize()).also { source.getState(it) }
            source.deactivate()
            source.destroy()

            val restored = host.instantiateNativePlugin(pluginInfo)
            restored.prepare(256, 48000, 0x10000)
            restored.activate()
            restored.setState(saved)
            assertEquals("a fresh instance restores it", identity, restored.awaitSoundFont(identity))
            restored.deactivate()
            restored.destroy()
        } finally {
            host.disconnectPluginService(pluginInfo.packageName)
            host.dispose()
        }
    }
}
