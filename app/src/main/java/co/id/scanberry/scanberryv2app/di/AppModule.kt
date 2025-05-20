package co.id.scanberry.scanberryv2app.di

import co.id.scanberry.scanberryv2app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Replace "YOUR_API_KEY" with BuildConfig.MY_API_KEY if you set it there
    @Provides @Singleton
    @Named("API_KEY")
    fun provideApiKey(): String = BuildConfig.API_KEY
}
