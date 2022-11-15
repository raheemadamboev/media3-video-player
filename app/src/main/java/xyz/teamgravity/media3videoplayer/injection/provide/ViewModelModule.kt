package xyz.teamgravity.media3videoplayer.injection.provide

import android.app.Application
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import xyz.teamgravity.media3videoplayer.core.util.MetaDataReader

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @Provides
    @ViewModelScoped
    fun provideExoPlayer(application: Application, audioAttributes: AudioAttributes): ExoPlayer =
        ExoPlayer.Builder(application).build().apply {
            setAudioAttributes(audioAttributes, true)
            setHandleAudioBecomingNoisy(true)
        }

    @Provides
    @ViewModelScoped
    fun provideMetaDataReader(application: Application): MetaDataReader = MetaDataReader(application)
}