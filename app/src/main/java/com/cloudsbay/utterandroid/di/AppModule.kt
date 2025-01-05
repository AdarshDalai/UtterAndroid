package com.cloudsbay.utterandroid.di

import android.content.Context
import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.comment.data.CommentService
import com.cloudsbay.utterandroid.comment.data.repository.CommentRepository
import com.cloudsbay.utterandroid.comment.data.repository.CommentRepositoryImpl
import com.cloudsbay.utterandroid.feed.data.FeedService
import com.cloudsbay.utterandroid.feed.data.repository.FeedRepository
import com.cloudsbay.utterandroid.feed.data.repository.FeedRepositoryImpl
import com.cloudsbay.utterandroid.like.data.LikeService
import com.cloudsbay.utterandroid.like.data.repository.LikeRepository
import com.cloudsbay.utterandroid.like.data.repository.LikeRepositoryImpl
import com.cloudsbay.utterandroid.network.KtorClient
import com.cloudsbay.utterandroid.post.data.api.PostDataStore
import com.cloudsbay.utterandroid.post.data.api.PostService
import com.cloudsbay.utterandroid.post.data.repository.PostRepository
import com.cloudsbay.utterandroid.post.data.repository.PostRepositoryImpl
import com.cloudsbay.utterandroid.profile.data.api.ProfileDataStore
import com.cloudsbay.utterandroid.profile.data.api.ProfileService
import com.cloudsbay.utterandroid.profile.data.repository.ProfileRepository
import com.cloudsbay.utterandroid.profile.data.repository.ProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun providesPostsService(ktorClient: KtorClient, tokenDataStore: TokenDataStore): PostService {
        return PostService(ktorClient, tokenDataStore)
    }

    @Provides
    @Singleton
    fun providesPostDataStore(@ApplicationContext context: Context): PostDataStore {
        return PostDataStore(context)

    }

    @Provides
    @Singleton
    fun postRepository(postService: PostService, postDataStore: PostDataStore): PostRepository {
        return PostRepositoryImpl(postService, postDataStore)
    }

    @Provides
    @Singleton
    fun providesProfileService(ktorClient: KtorClient, tokenDataStore: TokenDataStore): ProfileService {
        return ProfileService(ktorClient, tokenDataStore)
    }

    @Provides
    @Singleton
    fun providesProfileDataStore(@ApplicationContext context: Context): ProfileDataStore {
        return ProfileDataStore(context)

    }

    @Provides
    @Singleton
    fun providesProfileRepository(profileService: ProfileService, profileDataStore: ProfileDataStore): ProfileRepository {
        return ProfileRepositoryImpl(profileService, profileDataStore)
    }

    @Provides
    @Singleton
    fun providesCommentService(ktorClient: KtorClient, tokenDataStore: TokenDataStore): CommentService {
        return CommentService(ktorClient, tokenDataStore)
    }

    @Provides
    @Singleton
    fun providesCommentRepository(commentService: CommentService): CommentRepository {
        return CommentRepositoryImpl(commentService)
    }

    @Provides
    @Singleton
    fun providesLikeService(ktorClient: KtorClient, tokenDataStore: TokenDataStore): LikeService {
        return LikeService(ktorClient, tokenDataStore)
    }

    @Provides
    @Singleton
    fun providesLikeRepository(likeService: LikeService): LikeRepository {
        return LikeRepositoryImpl(likeService)
    }

    @Provides
    @Singleton
    fun providesFeedService(ktorClient: KtorClient, tokenDataStore: TokenDataStore): FeedService {
        return FeedService(ktorClient, tokenDataStore)
    }

    @Provides
    @Singleton
    fun providesFeedRepository(feedService: FeedService): FeedRepository {
        return FeedRepositoryImpl(feedService)
    }
}