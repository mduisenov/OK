package ru.ok.android.bus;

import android.annotation.SuppressLint;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import ru.ok.android.services.app.MusicService.Starter;
import ru.ok.android.services.processors.GetRecommendedFriendsProcessor;
import ru.ok.android.services.processors.GetStatusProcessor;
import ru.ok.android.services.processors.GuestProcessor;
import ru.ok.android.services.processors.PymkProcessor;
import ru.ok.android.services.processors.SearchQuickProcessor;
import ru.ok.android.services.processors.SetStatusProcessor;
import ru.ok.android.services.processors.calls.GetVideoCallParamsProcessor;
import ru.ok.android.services.processors.discussions.DiscussionAddProcessor;
import ru.ok.android.services.processors.discussions.DiscussionChunksProcessor;
import ru.ok.android.services.processors.discussions.DiscussionProcessor;
import ru.ok.android.services.processors.discussions.DiscussionsMarkAsReadProcessor;
import ru.ok.android.services.processors.discussions.MarkAsReadDiscussionsProcessor;
import ru.ok.android.services.processors.events.GetEventsProcessor;
import ru.ok.android.services.processors.friends.FriendsFilterProcessor;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.android.services.processors.friends.MutualFriendsProcessor;
import ru.ok.android.services.processors.gcm.GcmRegisterProcessor;
import ru.ok.android.services.processors.general.LikeProcessor;
import ru.ok.android.services.processors.general.RemoveOldDataProcessor;
import ru.ok.android.services.processors.general.RingtoneProcessor;
import ru.ok.android.services.processors.geo.ComplaintPlaceProcessor;
import ru.ok.android.services.processors.geo.GetCategoriesProcessor;
import ru.ok.android.services.processors.geo.GetPlacesProcessor;
import ru.ok.android.services.processors.geo.ReverseGeocodeProcessor;
import ru.ok.android.services.processors.geo.ValidatePlaceProcessor;
import ru.ok.android.services.processors.groups.GroupsProcessor;
import ru.ok.android.services.processors.login.ExpireSessionProcessor;
import ru.ok.android.services.processors.login.LogoutAllProcessor;
import ru.ok.android.services.processors.mediatopic.MediaTopicEditTextProcessor;
import ru.ok.android.services.processors.mediatopic.MediaTopicGetByPhotoProcessor;
import ru.ok.android.services.processors.mediatopic.MediatopicProcessor;
import ru.ok.android.services.processors.messaging.ConversationsProcessor;
import ru.ok.android.services.processors.messaging.MessagesChunksProcessor;
import ru.ok.android.services.processors.messaging.MessagesProcessor;
import ru.ok.android.services.processors.music.AddTrackProcessor;
import ru.ok.android.services.processors.music.DeleteTrackProcessor;
import ru.ok.android.services.processors.music.GetAlbumInfoProcessor;
import ru.ok.android.services.processors.music.GetAlbumTracksProcessor;
import ru.ok.android.services.processors.music.GetAlbumsForArtistProcessor;
import ru.ok.android.services.processors.music.GetArtistInfoProcessor;
import ru.ok.android.services.processors.music.GetArtistSimilarTracksProcessor;
import ru.ok.android.services.processors.music.GetArtistTrackProcessor;
import ru.ok.android.services.processors.music.GetCollectionInfoProcessor;
import ru.ok.android.services.processors.music.GetCollectionTracksProcessor;
import ru.ok.android.services.processors.music.GetCustomTrackProcessor;
import ru.ok.android.services.processors.music.GetHistoryMusicProcessor;
import ru.ok.android.services.processors.music.GetMyFriendsProcessor;
import ru.ok.android.services.processors.music.GetMyMusicCollectionsProcessor;
import ru.ok.android.services.processors.music.GetMyMusicProcessor;
import ru.ok.android.services.processors.music.GetPlayListInfoProcessor;
import ru.ok.android.services.processors.music.GetPlayTrackInfoProcessor;
import ru.ok.android.services.processors.music.GetPopCollectionTracksProcessor;
import ru.ok.android.services.processors.music.GetPopMusicCollectionsProcessor;
import ru.ok.android.services.processors.music.GetPopMusicProcessor;
import ru.ok.android.services.processors.music.GetRelevantProcessor;
import ru.ok.android.services.processors.music.GetSearchAlbumsProcessor;
import ru.ok.android.services.processors.music.GetSearchArtistsProcessor;
import ru.ok.android.services.processors.music.GetSearchMusicProcessor;
import ru.ok.android.services.processors.music.GetTunerTracksProcessor;
import ru.ok.android.services.processors.music.GetTunersProcessor;
import ru.ok.android.services.processors.music.GetUserMusicCollectionsProcessor;
import ru.ok.android.services.processors.music.GetUserMusicProcessor;
import ru.ok.android.services.processors.music.Play30MusicProcessor;
import ru.ok.android.services.processors.music.SetMusicStatusProcessor;
import ru.ok.android.services.processors.music.StatusPlayMusicProcessor;
import ru.ok.android.services.processors.music.SubscribeMusicCollectionProcessor;
import ru.ok.android.services.processors.music.UnSubscribeMusicCollectionProcessor;
import ru.ok.android.services.processors.notification.NotificationProcessor;
import ru.ok.android.services.processors.photo.CreatePhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.DeletePhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.DeletePhotoProcessor;
import ru.ok.android.services.processors.photo.EditPhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.EditPhotoProcessor;
import ru.ok.android.services.processors.photo.GetPhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.GetPhotoAlbumsProcessor;
import ru.ok.android.services.processors.photo.GetPhotoInfoProcessor;
import ru.ok.android.services.processors.photo.GetPhotoTagsProcessor;
import ru.ok.android.services.processors.photo.GetPhotosProcessor;
import ru.ok.android.services.processors.photo.ImageUploadNotificationProcessor;
import ru.ok.android.services.processors.photo.LikePhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.LikePhotoProcessor;
import ru.ok.android.services.processors.photo.MarkPhotoProcessor;
import ru.ok.android.services.processors.photo.MarkPhotoSpamProcessor;
import ru.ok.android.services.processors.photo.SetAlbumMainPhotoProcessor;
import ru.ok.android.services.processors.photo.SetMainPhotoProcessor;
import ru.ok.android.services.processors.photo.upload.ImageUploadProcessor;
import ru.ok.android.services.processors.photo.upload.StoreLastSuccessfulImageUploadTimeProcessor;
import ru.ok.android.services.processors.photo.view.DeleteUserPhotoTagProcessor;
import ru.ok.android.services.processors.photo.view.GetAlbumInfoBatchProcessor;
import ru.ok.android.services.processors.photo.view.GetFullPhotoInfoProcessor;
import ru.ok.android.services.processors.photo.view.GetPhotoAlbumsBatchProcessor;
import ru.ok.android.services.processors.photo.view.GetViewInfoBatchProcessor;
import ru.ok.android.services.processors.poll.AppPollProcessor;
import ru.ok.android.services.processors.presents.GetPresentsProcessor;
import ru.ok.android.services.processors.presents.ReceivePresentProcessor;
import ru.ok.android.services.processors.presents.SendPresentProcessor;
import ru.ok.android.services.processors.registration.AuthorizationSettingsProcessor;
import ru.ok.android.services.processors.registration.ChangePasswordProcessor;
import ru.ok.android.services.processors.registration.RegisterWithLibVerifyProcessor;
import ru.ok.android.services.processors.settings.MediaComposerSettingsProcessor;
import ru.ok.android.services.processors.settings.ServicesSettingsProcessor;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor;
import ru.ok.android.services.processors.stickers.StickersProcessor;
import ru.ok.android.services.processors.stream.GetStreamProcessor;
import ru.ok.android.services.processors.stream.StreamMiscProcessor;
import ru.ok.android.services.processors.users.CurrentUserInfoProcessor;
import ru.ok.android.services.processors.users.UsersProcessor;
import ru.ok.android.services.processors.video.GetSimilarMoviesProcessor;
import ru.ok.android.services.processors.video.VideoLikeProcessor;
import ru.ok.android.services.processors.video.VideoProcessor;

@SuppressLint({"ResourceType"})
public final class GlobalBusRegistrar {
    public static final GlobalBusRegistrar INSTANCE;

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.100 */
    class AnonymousClass100 implements Subscriber {
        final /* synthetic */ AddTrackProcessor val$target;

        AnonymousClass100(AddTrackProcessor addTrackProcessor) {
            this.val$target = addTrackProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624038:
                    this.val$target.addTrack((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.101 */
    class AnonymousClass101 implements Subscriber {
        final /* synthetic */ GetUserMusicCollectionsProcessor val$target;

        AnonymousClass101(GetUserMusicCollectionsProcessor getUserMusicCollectionsProcessor) {
            this.val$target = getUserMusicCollectionsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624076:
                    this.val$target.getUserMusicCollections((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.102 */
    class AnonymousClass102 implements Subscriber {
        final /* synthetic */ SubscribeMusicCollectionProcessor val$target;

        AnonymousClass102(SubscribeMusicCollectionProcessor subscribeMusicCollectionProcessor) {
            this.val$target = subscribeMusicCollectionProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624082:
                    this.val$target.subscribeCollection((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.103 */
    class AnonymousClass103 implements Subscriber {
        final /* synthetic */ GetPopMusicProcessor val$target;

        AnonymousClass103(GetPopMusicProcessor getPopMusicProcessor) {
            this.val$target = getPopMusicProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624063:
                    this.val$target.getPopMusic((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.104 */
    class AnonymousClass104 implements Subscriber {
        final /* synthetic */ Play30MusicProcessor val$target;

        AnonymousClass104(Play30MusicProcessor play30MusicProcessor) {
            this.val$target = play30MusicProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624080:
                    this.val$target.play30Music((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.105 */
    class AnonymousClass105 implements Subscriber {
        final /* synthetic */ GetRelevantProcessor val$target;

        AnonymousClass105(GetRelevantProcessor getRelevantProcessor) {
            this.val$target = getRelevantProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624069:
                    this.val$target.getRelevant((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.106 */
    class AnonymousClass106 implements Subscriber {
        final /* synthetic */ GetArtistTrackProcessor val$target;

        AnonymousClass106(GetArtistTrackProcessor getArtistTrackProcessor) {
            this.val$target = getArtistTrackProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624047:
                    this.val$target.getArtistTrack((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.107 */
    class AnonymousClass107 implements Subscriber {
        final /* synthetic */ NotificationProcessor val$target;

        AnonymousClass107(NotificationProcessor notificationProcessor) {
            this.val$target = notificationProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624093:
                    this.val$target.onNotificationReceived((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.108 */
    class AnonymousClass108 implements Subscriber {
        final /* synthetic */ SendPresentProcessor val$target;

        AnonymousClass108(SendPresentProcessor sendPresentProcessor) {
            this.val$target = sendPresentProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624013:
                    this.val$target.loadPresentAndUser((BusEvent) event);
                case 2131624100:
                    this.val$target.sendPresent((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.109 */
    class AnonymousClass109 implements Subscriber {
        final /* synthetic */ GetPresentsProcessor val$target;

        AnonymousClass109(GetPresentsProcessor getPresentsProcessor) {
            this.val$target = getPresentsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624012:
                    this.val$target.loadPresents((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.10 */
    class AnonymousClass10 implements Subscriber {
        final /* synthetic */ ComplaintPlaceProcessor val$target;

        AnonymousClass10(ComplaintPlaceProcessor complaintPlaceProcessor) {
            this.val$target = complaintPlaceProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624023:
                    this.val$target.complaintPlace((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.110 */
    class AnonymousClass110 implements Subscriber {
        final /* synthetic */ ReceivePresentProcessor val$target;

        AnonymousClass110(ReceivePresentProcessor receivePresentProcessor) {
            this.val$target = receivePresentProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623947:
                    this.val$target.acceptPresent((BusEvent) event);
                case 2131623962:
                    this.val$target.declinePresent((BusEvent) event);
                case 2131624014:
                    this.val$target.loadPresentNotification((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.111 */
    class AnonymousClass111 implements Subscriber {
        final /* synthetic */ GroupsProcessor val$target;

        AnonymousClass111(GroupsProcessor groupsProcessor) {
            this.val$target = groupsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623955:
                    this.val$target.getCommunityMembers((BusEvent) event);
                case 2131623956:
                    this.val$target.complaintToGroup((BusEvent) event);
                case 2131623990:
                    this.val$target.joinCommunity((BusEvent) event);
                case 2131623991:
                    this.val$target.groupCreate((BusEvent) event);
                case 2131623992:
                    this.val$target.friendsInGroup((BusEvent) event);
                case 2131623993:
                    this.val$target.getGroupMembers((BusEvent) event);
                case 2131623994:
                    this.val$target.getGroupInfo((BusEvent) event);
                case 2131623995:
                    this.val$target.groupInviteFriends((BusEvent) event);
                case 2131623996:
                    this.val$target.groupJoin((BusEvent) event);
                case 2131623997:
                    this.val$target.groupLeave((BusEvent) event);
                case 2131624105:
                    this.val$target.subscribeToStream((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.112 */
    class AnonymousClass112 implements Subscriber {
        final /* synthetic */ ExpireSessionProcessor val$target;

        AnonymousClass112(ExpireSessionProcessor expireSessionProcessor) {
            this.val$target = expireSessionProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624041:
                    this.val$target.expireSession((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.113 */
    class AnonymousClass113 implements Subscriber {
        final /* synthetic */ LogoutAllProcessor val$target;

        AnonymousClass113(LogoutAllProcessor logoutAllProcessor) {
            this.val$target = logoutAllProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623985:
                    this.val$target.logoutAll((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.11 */
    class AnonymousClass11 implements Subscriber {
        final /* synthetic */ GetPlacesProcessor val$target;

        AnonymousClass11(GetPlacesProcessor getPlacesProcessor) {
            this.val$target = getPlacesProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624029:
                    this.val$target.getPlaces((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.12 */
    class AnonymousClass12 implements Subscriber {
        final /* synthetic */ ReverseGeocodeProcessor val$target;

        AnonymousClass12(ReverseGeocodeProcessor reverseGeocodeProcessor) {
            this.val$target = reverseGeocodeProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624035:
                    this.val$target.reverseGeoCode((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.13 */
    class AnonymousClass13 implements Subscriber {
        final /* synthetic */ GetCategoriesProcessor val$target;

        AnonymousClass13(GetCategoriesProcessor getCategoriesProcessor) {
            this.val$target = getCategoriesProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624028:
                    this.val$target.reverseGeoCode((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.14 */
    class AnonymousClass14 implements Subscriber {
        final /* synthetic */ AppPollProcessor val$target;

        AnonymousClass14(AppPollProcessor appPollProcessor) {
            this.val$target = appPollProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623983:
                    this.val$target.loadAppPolls((BusEvent) event);
                case 2131624099:
                    this.val$target.saveAnswers((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.15 */
    class AnonymousClass15 implements Subscriber {
        final /* synthetic */ UsersProcessor val$target;

        AnonymousClass15(UsersProcessor usersProcessor) {
            this.val$target = usersProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623957:
                    this.val$target.complaintToUser((BusEvent) event);
                case 2131624039:
                    this.val$target.deleteUserStatus((BusEvent) event);
                case 2131624074:
                    this.val$target.getUserInfo((BusEvent) event);
                case 2131624111:
                    this.val$target.subscribeToStream((BusEvent) event);
                case 2131624120:
                    this.val$target.getUserCounters((BusEvent) event);
                case 2131624121:
                    this.val$target.deleteFriend((BusEvent) event);
                case 2131624122:
                    this.val$target.inviteFriend((BusEvent) event);
                case 2131624123:
                    this.val$target.inviteFriends((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.16 */
    class AnonymousClass16 implements Subscriber {
        final /* synthetic */ CurrentUserInfoProcessor val$target;

        AnonymousClass16(CurrentUserInfoProcessor currentUserInfoProcessor) {
            this.val$target = currentUserInfoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624051:
                    this.val$target.getCurrentUserInfo((BusEvent) event);
                case 2131624052:
                    this.val$target.getCurrentUserInfoNew((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.17 */
    class AnonymousClass17 implements Subscriber {
        final /* synthetic */ GetStreamProcessor val$target;

        AnonymousClass17(GetStreamProcessor getStreamProcessor) {
            this.val$target = getStreamProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624017:
                    this.val$target.markStreamAllRead((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.18 */
    class AnonymousClass18 implements Subscriber {
        final /* synthetic */ StreamMiscProcessor val$target;

        AnonymousClass18(StreamMiscProcessor streamMiscProcessor) {
            this.val$target = streamMiscProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624107:
                    this.val$target.feedMarkAsSpam((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.19 */
    class AnonymousClass19 implements Subscriber {
        final /* synthetic */ GetFriendsProcessor val$target;

        AnonymousClass19(GetFriendsProcessor getFriendsProcessor) {
            this.val$target = getFriendsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623986:
                    this.val$target.getOnlineFriends((BusEvent) event);
                case 2131624119:
                    this.val$target.getFriends((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.1 */
    class C02391 implements Subscriber {
        final /* synthetic */ Starter val$target;

        C02391(Starter starter) {
            this.val$target = starter;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624086:
                    this.val$target.pause((BusEvent) event);
                case 2131624108:
                    this.val$target.state((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.20 */
    class AnonymousClass20 implements Subscriber {
        final /* synthetic */ MutualFriendsProcessor val$target;

        AnonymousClass20(MutualFriendsProcessor mutualFriendsProcessor) {
            this.val$target = mutualFriendsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624092:
                    this.val$target.process((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.21 */
    class AnonymousClass21 implements Subscriber {
        final /* synthetic */ FriendsFilterProcessor val$target;

        AnonymousClass21(FriendsFilterProcessor friendsFilterProcessor) {
            this.val$target = friendsFilterProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623981:
                    this.val$target.requestFriendsFilter((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.22 */
    class AnonymousClass22 implements Subscriber {
        final /* synthetic */ GetSimilarMoviesProcessor val$target;

        AnonymousClass22(GetSimilarMoviesProcessor getSimilarMoviesProcessor) {
            this.val$target = getSimilarMoviesProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624125:
                    this.val$target.getSimilarMoviesInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.24 */
    class AnonymousClass24 implements Subscriber {
        final /* synthetic */ VideoProcessor val$target;

        AnonymousClass24(VideoProcessor videoProcessor) {
            this.val$target = videoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624124:
                    this.val$target.getVideosInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.25 */
    class AnonymousClass25 implements Subscriber {
        final /* synthetic */ VideoLikeProcessor val$target;

        AnonymousClass25(VideoLikeProcessor videoLikeProcessor) {
            this.val$target = videoLikeProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624010:
                    this.val$target.like((BusEvent) event);
                case 2131624118:
                    this.val$target.unlike((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.26 */
    class AnonymousClass26 implements Subscriber {
        final /* synthetic */ ChangePasswordProcessor val$target;

        AnonymousClass26(ChangePasswordProcessor changePasswordProcessor) {
            this.val$target = changePasswordProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623949:
                    this.val$target.changePassword((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.27 */
    class AnonymousClass27 implements Subscriber {
        final /* synthetic */ AuthorizationSettingsProcessor val$target;

        AnonymousClass27(AuthorizationSettingsProcessor authorizationSettingsProcessor) {
            this.val$target = authorizationSettingsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623948:
                    this.val$target.getLoginVersion();
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.28 */
    class AnonymousClass28 implements Subscriber {
        final /* synthetic */ RegisterWithLibVerifyProcessor val$target;

        AnonymousClass28(RegisterWithLibVerifyProcessor registerWithLibVerifyProcessor) {
            this.val$target = registerWithLibVerifyProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624095:
                    this.val$target.registerWithLibVerify((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.29 */
    class AnonymousClass29 implements Subscriber {
        final /* synthetic */ ConversationsProcessor val$target;

        AnonymousClass29(ConversationsProcessor conversationsProcessor) {
            this.val$target = conversationsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623959:
                    this.val$target.markAsRead((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.2 */
    class C02402 implements Subscriber {
        final /* synthetic */ SetStatusProcessor val$target;

        C02402(SetStatusProcessor setStatusProcessor) {
            this.val$target = setStatusProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624116:
                    this.val$target.updateStates((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.30 */
    class AnonymousClass30 implements Subscriber {
        final /* synthetic */ MessagesProcessor val$target;

        AnonymousClass30(MessagesProcessor messagesProcessor) {
            this.val$target = messagesProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623950:
                    this.val$target.addParticipants((BusEvent) event);
                case 2131623951:
                    this.val$target.createChat((BusEvent) event);
                case 2131623952:
                    this.val$target.kickUser((BusEvent) event);
                case 2131623953:
                    this.val$target.leaveChat((BusEvent) event);
                case 2131623954:
                    this.val$target.setTopic((BusEvent) event);
                case 2131623958:
                    this.val$target.deleteConversation((BusEvent) event);
                case 2131623960:
                    this.val$target.updateConversation((BusEvent) event);
                case 2131624025:
                    this.val$target.deleteMessages((BusEvent) event);
                case 2131624033:
                    this.val$target.loadOneMessage((BusEvent) event);
                case 2131624036:
                    this.val$target.spamMessages((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.31 */
    class AnonymousClass31 implements Subscriber {
        final /* synthetic */ MessagesChunksProcessor val$target;

        AnonymousClass31(MessagesChunksProcessor messagesChunksProcessor) {
            this.val$target = messagesChunksProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624030:
                    this.val$target.getMessagesUpdates((BusEvent) event);
                case 2131624031:
                    this.val$target.loadFirstPortion((BusEvent) event);
                case 2131624032:
                    this.val$target.loadNextMessages((BusEvent) event);
                case 2131624034:
                    this.val$target.loadPreviousMessages((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.32 */
    class AnonymousClass32 implements Subscriber {
        final /* synthetic */ MediaTopicGetByPhotoProcessor val$target;

        AnonymousClass32(MediaTopicGetByPhotoProcessor mediaTopicGetByPhotoProcessor) {
            this.val$target = mediaTopicGetByPhotoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624091:
                    this.val$target.process((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.33 */
    class AnonymousClass33 implements Subscriber {
        final /* synthetic */ MediaTopicEditTextProcessor val$target;

        AnonymousClass33(MediaTopicEditTextProcessor mediaTopicEditTextProcessor) {
            this.val$target = mediaTopicEditTextProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624090:
                    this.val$target.editMediaTopicText((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.34 */
    class AnonymousClass34 implements Subscriber {
        final /* synthetic */ MediatopicProcessor val$target;

        AnonymousClass34(MediatopicProcessor mediatopicProcessor) {
            this.val$target = mediatopicProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624018:
                    this.val$target.delete((BusEvent) event);
                case 2131624019:
                    this.val$target.pin((BusEvent) event);
                case 2131624020:
                    this.val$target.setToStatus((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.35 */
    class AnonymousClass35 implements Subscriber {
        final /* synthetic */ StickersProcessor val$target;

        AnonymousClass35(StickersProcessor stickersProcessor) {
            this.val$target = stickersProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624103:
                    this.val$target.updateStickerSets();
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.36 */
    class AnonymousClass36 implements Subscriber {
        final /* synthetic */ MarkAsReadDiscussionsProcessor val$target;

        AnonymousClass36(MarkAsReadDiscussionsProcessor markAsReadDiscussionsProcessor) {
            this.val$target = markAsReadDiscussionsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624079:
                    this.val$target.markAsReadDiscussions((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.37 */
    class AnonymousClass37 implements Subscriber {
        final /* synthetic */ DiscussionAddProcessor val$target;

        AnonymousClass37(DiscussionAddProcessor discussionAddProcessor) {
            this.val$target = discussionAddProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623963:
                    this.val$target.addComment((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.38 */
    class AnonymousClass38 implements Subscriber {
        final /* synthetic */ DiscussionsMarkAsReadProcessor val$target;

        AnonymousClass38(DiscussionsMarkAsReadProcessor discussionsMarkAsReadProcessor) {
            this.val$target = discussionsMarkAsReadProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623970:
                    this.val$target.markAsRead((BusEvent) event);
                case 2131623971:
                    this.val$target.markDiscussionAsRead((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.39 */
    class AnonymousClass39 implements Subscriber {
        final /* synthetic */ DiscussionChunksProcessor val$target;

        AnonymousClass39(DiscussionChunksProcessor discussionChunksProcessor) {
            this.val$target = discussionChunksProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623967:
                    this.val$target.loadFirstCommentsPortion((BusEvent) event);
                case 2131623968:
                    this.val$target.loadNextCommentsPortion((BusEvent) event);
                case 2131623969:
                    this.val$target.loadPreviousCommentsPortion((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.3 */
    class C02413 implements Subscriber {
        final /* synthetic */ GetRecommendedFriendsProcessor val$target;

        C02413(GetRecommendedFriendsProcessor getRecommendedFriendsProcessor) {
            this.val$target = getRecommendedFriendsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624065:
                    this.val$target.getRecommendedFriends((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.40 */
    class AnonymousClass40 implements Subscriber {
        final /* synthetic */ DiscussionProcessor val$target;

        AnonymousClass40(DiscussionProcessor discussionProcessor) {
            this.val$target = discussionProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623964:
                    this.val$target.deleteComments((BusEvent) event);
                case 2131623965:
                    this.val$target.addDiscussionLike((BusEvent) event);
                case 2131623966:
                    this.val$target.addCommentLike((BusEvent) event);
                case 2131623972:
                    this.val$target.spamComments((BusEvent) event);
                case 2131623973:
                    this.val$target.editComment((BusEvent) event);
                case 2131623974:
                    this.val$target.editCommentUndo((BusEvent) event);
                case 2131623975:
                    this.val$target.loadOneComment((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.41 */
    class AnonymousClass41 implements Subscriber {
        final /* synthetic */ SetAlbumMainPhotoProcessor val$target;

        AnonymousClass41(SetAlbumMainPhotoProcessor setAlbumMainPhotoProcessor) {
            this.val$target = setAlbumMainPhotoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624114:
                    this.val$target.setAlbumMainPhoto((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.42 */
    class AnonymousClass42 implements Subscriber {
        final /* synthetic */ GetPhotoAlbumsProcessor val$target;

        AnonymousClass42(GetPhotoAlbumsProcessor getPhotoAlbumsProcessor) {
            this.val$target = getPhotoAlbumsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624002:
                    this.val$target.getPhotoAlbums((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.43 */
    class AnonymousClass43 implements Subscriber {
        final /* synthetic */ ImageUploadNotificationProcessor val$target;

        AnonymousClass43(ImageUploadNotificationProcessor imageUploadNotificationProcessor) {
            this.val$target = imageUploadNotificationProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624007:
                    this.val$target.onClearAllRequest((BusEvent) event);
                case 2131624008:
                    this.val$target.onClearErrorsRequest((BusEvent) event);
                case 2131624225:
                    this.val$target.onImageUploaderEvent((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.44 */
    class AnonymousClass44 implements Subscriber {
        final /* synthetic */ MarkPhotoSpamProcessor val$target;

        AnonymousClass44(MarkPhotoSpamProcessor markPhotoSpamProcessor) {
            this.val$target = markPhotoSpamProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624089:
                    this.val$target.markPhotoSpam((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.45 */
    class AnonymousClass45 implements Subscriber {
        final /* synthetic */ GetPhotosProcessor val$target;

        AnonymousClass45(GetPhotosProcessor getPhotosProcessor) {
            this.val$target = getPhotosProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624005:
                    this.val$target.getPhotos((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.46 */
    class AnonymousClass46 implements Subscriber {
        final /* synthetic */ DeletePhotoProcessor val$target;

        AnonymousClass46(DeletePhotoProcessor deletePhotoProcessor) {
            this.val$target = deletePhotoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623977:
                    this.val$target.deletePhoto((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.47 */
    class AnonymousClass47 implements Subscriber {
        final /* synthetic */ LikePhotoProcessor val$target;

        AnonymousClass47(LikePhotoProcessor likePhotoProcessor) {
            this.val$target = likePhotoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624016:
                    this.val$target.likePhoto((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.48 */
    class AnonymousClass48 implements Subscriber {
        final /* synthetic */ GetPhotoAlbumProcessor val$target;

        AnonymousClass48(GetPhotoAlbumProcessor getPhotoAlbumProcessor) {
            this.val$target = getPhotoAlbumProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624000:
                    this.val$target.getAlbumInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.49 */
    class AnonymousClass49 implements Subscriber {
        final /* synthetic */ SetMainPhotoProcessor val$target;

        AnonymousClass49(SetMainPhotoProcessor setMainPhotoProcessor) {
            this.val$target = setMainPhotoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624115:
                    this.val$target.setMainPhoto((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.4 */
    class C02424 implements Subscriber {
        final /* synthetic */ PymkProcessor val$target;

        C02424(PymkProcessor pymkProcessor) {
            this.val$target = pymkProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623988:
                    this.val$target.loadPymk((BusEvent) event);
                case 2131623989:
                    this.val$target.process((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.50 */
    class AnonymousClass50 implements Subscriber {
        final /* synthetic */ EditPhotoAlbumProcessor val$target;

        AnonymousClass50(EditPhotoAlbumProcessor editPhotoAlbumProcessor) {
            this.val$target = editPhotoAlbumProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623979:
                    this.val$target.editPhotoAlbum((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.51 */
    class AnonymousClass51 implements Subscriber {
        final /* synthetic */ GetPhotoTagsProcessor val$target;

        AnonymousClass51(GetPhotoTagsProcessor getPhotoTagsProcessor) {
            this.val$target = getPhotoTagsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624004:
                    this.val$target.getPhotoTags((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.52 */
    class AnonymousClass52 implements Subscriber {
        final /* synthetic */ LikePhotoAlbumProcessor val$target;

        AnonymousClass52(LikePhotoAlbumProcessor likePhotoAlbumProcessor) {
            this.val$target = likePhotoAlbumProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624015:
                    this.val$target.likePhotoAlbum((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.53 */
    class AnonymousClass53 implements Subscriber {
        final /* synthetic */ EditPhotoProcessor val$target;

        AnonymousClass53(EditPhotoProcessor editPhotoProcessor) {
            this.val$target = editPhotoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623980:
                    this.val$target.editPhoto((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.54 */
    class AnonymousClass54 implements Subscriber {
        final /* synthetic */ DeletePhotoAlbumProcessor val$target;

        AnonymousClass54(DeletePhotoAlbumProcessor deletePhotoAlbumProcessor) {
            this.val$target = deletePhotoAlbumProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623976:
                    this.val$target.deletePhotoAlbum((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.55 */
    class AnonymousClass55 implements Subscriber {
        final /* synthetic */ MarkPhotoProcessor val$target;

        AnonymousClass55(MarkPhotoProcessor markPhotoProcessor) {
            this.val$target = markPhotoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624088:
                    this.val$target.markPhoto((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.56 */
    class AnonymousClass56 implements Subscriber {
        final /* synthetic */ CreatePhotoAlbumProcessor val$target;

        AnonymousClass56(CreatePhotoAlbumProcessor createPhotoAlbumProcessor) {
            this.val$target = createPhotoAlbumProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623961:
                    this.val$target.createPhotoAlbum((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.57 */
    class AnonymousClass57 implements Subscriber {
        final /* synthetic */ GetPhotoInfoProcessor val$target;

        AnonymousClass57(GetPhotoInfoProcessor getPhotoInfoProcessor) {
            this.val$target = getPhotoInfoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624003:
                    this.val$target.getPhotoInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.58 */
    class AnonymousClass58 implements Subscriber {
        final /* synthetic */ GetPhotoAlbumsBatchProcessor val$target;

        AnonymousClass58(GetPhotoAlbumsBatchProcessor getPhotoAlbumsBatchProcessor) {
            this.val$target = getPhotoAlbumsBatchProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624001:
                    this.val$target.getPhotoAlbumsBatch((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.59 */
    class AnonymousClass59 implements Subscriber {
        final /* synthetic */ GetAlbumInfoBatchProcessor val$target;

        AnonymousClass59(GetAlbumInfoBatchProcessor getAlbumInfoBatchProcessor) {
            this.val$target = getAlbumInfoBatchProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623998:
                    this.val$target.getAlbumInfoBatch((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.5 */
    class C02435 implements Subscriber {
        final /* synthetic */ GuestProcessor val$target;

        C02435(GuestProcessor guestProcessor) {
            this.val$target = guestProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623984:
                    this.val$target.loadGuest((BusEvent) event);
                case 2131624096:
                    this.val$target.removeGuest((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.60 */
    class AnonymousClass60 implements Subscriber {
        final /* synthetic */ GetViewInfoBatchProcessor val$target;

        AnonymousClass60(GetViewInfoBatchProcessor getViewInfoBatchProcessor) {
            this.val$target = getViewInfoBatchProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624006:
                    this.val$target.getViewInfoBatch((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.61 */
    class AnonymousClass61 implements Subscriber {
        final /* synthetic */ GetFullPhotoInfoProcessor val$target;

        AnonymousClass61(GetFullPhotoInfoProcessor getFullPhotoInfoProcessor) {
            this.val$target = getFullPhotoInfoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623999:
                    this.val$target.getFullPhotoInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.62 */
    class AnonymousClass62 implements Subscriber {
        final /* synthetic */ DeleteUserPhotoTagProcessor val$target;

        AnonymousClass62(DeleteUserPhotoTagProcessor deleteUserPhotoTagProcessor) {
            this.val$target = deleteUserPhotoTagProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623978:
                    this.val$target.deletePhotoTag((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.63 */
    class AnonymousClass63 implements Subscriber {
        final /* synthetic */ ImageUploadProcessor val$target;

        AnonymousClass63(ImageUploadProcessor imageUploadProcessor) {
            this.val$target = imageUploadProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624084:
                    this.val$target.imageUpload((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.64 */
    class AnonymousClass64 implements Subscriber {
        final /* synthetic */ StoreLastSuccessfulImageUploadTimeProcessor val$target;

        AnonymousClass64(StoreLastSuccessfulImageUploadTimeProcessor storeLastSuccessfulImageUploadTimeProcessor) {
            this.val$target = storeLastSuccessfulImageUploadTimeProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624225:
                    this.val$target.onImageUploadEvent((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.65 */
    class AnonymousClass65 implements Subscriber {
        final /* synthetic */ RingtoneProcessor val$target;

        AnonymousClass65(RingtoneProcessor ringtoneProcessor) {
            this.val$target = ringtoneProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624098:
                    this.val$target.extractRingtones((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.66 */
    class AnonymousClass66 implements Subscriber {
        final /* synthetic */ RemoveOldDataProcessor val$target;

        AnonymousClass66(RemoveOldDataProcessor removeOldDataProcessor) {
            this.val$target = removeOldDataProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624097:
                    this.val$target.removeOldData((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.67 */
    class AnonymousClass67 implements Subscriber {
        final /* synthetic */ LikeProcessor val$target;

        AnonymousClass67(LikeProcessor likeProcessor) {
            this.val$target = likeProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624009:
                    this.val$target.like((BusEvent) event);
                case 2131624117:
                    this.val$target.unlike((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.68 */
    class AnonymousClass68 implements Subscriber {
        final /* synthetic */ GetEventsProcessor val$target;

        AnonymousClass68(GetEventsProcessor getEventsProcessor) {
            this.val$target = getEventsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623987:
                    this.val$target.getPromoLinks((BusEvent) event);
                case 2131624053:
                    this.val$target.getEvents((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.69 */
    class AnonymousClass69 implements Subscriber {
        final /* synthetic */ ServicesSettingsProcessor val$target;

        AnonymousClass69(ServicesSettingsProcessor servicesSettingsProcessor) {
            this.val$target = servicesSettingsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624101:
                    this.val$target.getServicesSettings((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.6 */
    class C02446 implements Subscriber {
        final /* synthetic */ SearchQuickProcessor val$target;

        C02446(SearchQuickProcessor searchQuickProcessor) {
            this.val$target = searchQuickProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624113:
                    this.val$target.process((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.70 */
    class AnonymousClass70 implements Subscriber {
        final /* synthetic */ StartSettingsGetProcessor val$target;

        AnonymousClass70(StartSettingsGetProcessor startSettingsGetProcessor) {
            this.val$target = startSettingsGetProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624102:
                    this.val$target.getStartSettings((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.71 */
    class AnonymousClass71 implements Subscriber {
        final /* synthetic */ MediaComposerSettingsProcessor val$target;

        AnonymousClass71(MediaComposerSettingsProcessor mediaComposerSettingsProcessor) {
            this.val$target = mediaComposerSettingsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624022:
                    this.val$target.getMediaComposerSettings((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.72 */
    class AnonymousClass72 implements Subscriber {
        final /* synthetic */ GetVideoCallParamsProcessor val$target;

        AnonymousClass72(GetVideoCallParamsProcessor getVideoCallParamsProcessor) {
            this.val$target = getVideoCallParamsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624048:
                    this.val$target.getVideoCallParams((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.73 */
    class AnonymousClass73 implements Subscriber {
        final /* synthetic */ GcmRegisterProcessor val$target;

        AnonymousClass73(GcmRegisterProcessor gcmRegisterProcessor) {
            this.val$target = gcmRegisterProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131623982:
                    this.val$target.gcmRegister((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.74 */
    class AnonymousClass74 implements Subscriber {
        final /* synthetic */ DeleteTrackProcessor val$target;

        AnonymousClass74(DeleteTrackProcessor deleteTrackProcessor) {
            this.val$target = deleteTrackProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624040:
                    this.val$target.deleteTrack((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.75 */
    class AnonymousClass75 implements Subscriber {
        final /* synthetic */ GetCollectionTracksProcessor val$target;

        AnonymousClass75(GetCollectionTracksProcessor getCollectionTracksProcessor) {
            this.val$target = getCollectionTracksProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624050:
                    this.val$target.getCollectionTracks((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.76 */
    class AnonymousClass76 implements Subscriber {
        final /* synthetic */ GetArtistInfoProcessor val$target;

        AnonymousClass76(GetArtistInfoProcessor getArtistInfoProcessor) {
            this.val$target = getArtistInfoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624045:
                    this.val$target.getArtistInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.77 */
    class AnonymousClass77 implements Subscriber {
        final /* synthetic */ GetAlbumsForArtistProcessor val$target;

        AnonymousClass77(GetAlbumsForArtistProcessor getAlbumsForArtistProcessor) {
            this.val$target = getAlbumsForArtistProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624044:
                    this.val$target.getAlbumsForArtist((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.78 */
    class AnonymousClass78 implements Subscriber {
        final /* synthetic */ GetMyMusicCollectionsProcessor val$target;

        AnonymousClass78(GetMyMusicCollectionsProcessor getMyMusicCollectionsProcessor) {
            this.val$target = getMyMusicCollectionsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624058:
                    this.val$target.getMyMusicCollections((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.79 */
    class AnonymousClass79 implements Subscriber {
        final /* synthetic */ GetPopCollectionTracksProcessor val$target;

        AnonymousClass79(GetPopCollectionTracksProcessor getPopCollectionTracksProcessor) {
            this.val$target = getPopCollectionTracksProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624062:
                    this.val$target.getPopCollectionTracks((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.80 */
    class AnonymousClass80 implements Subscriber {
        final /* synthetic */ GetSearchAlbumsProcessor val$target;

        AnonymousClass80(GetSearchAlbumsProcessor getSearchAlbumsProcessor) {
            this.val$target = getSearchAlbumsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624066:
                    this.val$target.getSearchAlbums((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.81 */
    class AnonymousClass81 implements Subscriber {
        final /* synthetic */ GetPopMusicCollectionsProcessor val$target;

        AnonymousClass81(GetPopMusicCollectionsProcessor getPopMusicCollectionsProcessor) {
            this.val$target = getPopMusicCollectionsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624064:
                    this.val$target.getPopMusicCollections((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.82 */
    class AnonymousClass82 implements Subscriber {
        final /* synthetic */ GetAlbumInfoProcessor val$target;

        AnonymousClass82(GetAlbumInfoProcessor getAlbumInfoProcessor) {
            this.val$target = getAlbumInfoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624042:
                    this.val$target.getAlbumInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.83 */
    class AnonymousClass83 implements Subscriber {
        final /* synthetic */ GetPlayListInfoProcessor val$target;

        AnonymousClass83(GetPlayListInfoProcessor getPlayListInfoProcessor) {
            this.val$target = getPlayListInfoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624060:
                    this.val$target.getPlayListInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.84 */
    class AnonymousClass84 implements Subscriber {
        final /* synthetic */ GetMyMusicProcessor val$target;

        AnonymousClass84(GetMyMusicProcessor getMyMusicProcessor) {
            this.val$target = getMyMusicProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624057:
                    this.val$target.getMyMusic((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.85 */
    class AnonymousClass85 implements Subscriber {
        final /* synthetic */ StatusPlayMusicProcessor val$target;

        AnonymousClass85(StatusPlayMusicProcessor statusPlayMusicProcessor) {
            this.val$target = statusPlayMusicProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624087:
                    this.val$target.playStatusMusic((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.86 */
    class AnonymousClass86 implements Subscriber {
        final /* synthetic */ GetPlayTrackInfoProcessor val$target;

        AnonymousClass86(GetPlayTrackInfoProcessor getPlayTrackInfoProcessor) {
            this.val$target = getPlayTrackInfoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624061:
                    this.val$target.getPlayTrackInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.87 */
    class AnonymousClass87 implements Subscriber {
        final /* synthetic */ GetMyFriendsProcessor val$target;

        AnonymousClass87(GetMyFriendsProcessor getMyFriendsProcessor) {
            this.val$target = getMyFriendsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624059:
                    this.val$target.getMyFriends((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.88 */
    class AnonymousClass88 implements Subscriber {
        final /* synthetic */ GetArtistSimilarTracksProcessor val$target;

        AnonymousClass88(GetArtistSimilarTracksProcessor getArtistSimilarTracksProcessor) {
            this.val$target = getArtistSimilarTracksProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624046:
                    this.val$target.getArtistSimilarTracks((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.89 */
    class AnonymousClass89 implements Subscriber {
        final /* synthetic */ GetTunerTracksProcessor val$target;

        AnonymousClass89(GetTunerTracksProcessor getTunerTracksProcessor) {
            this.val$target = getTunerTracksProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624073:
                    this.val$target.getTunerTracks((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.8 */
    class C02458 implements Subscriber {
        final /* synthetic */ GetStatusProcessor val$target;

        C02458(GetStatusProcessor getStatusProcessor) {
            this.val$target = getStatusProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624070:
                    this.val$target.getStatus((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.90 */
    class AnonymousClass90 implements Subscriber {
        final /* synthetic */ GetUserMusicProcessor val$target;

        AnonymousClass90(GetUserMusicProcessor getUserMusicProcessor) {
            this.val$target = getUserMusicProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624075:
                    this.val$target.getUserMusic((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.91 */
    class AnonymousClass91 implements Subscriber {
        final /* synthetic */ SetMusicStatusProcessor val$target;

        AnonymousClass91(SetMusicStatusProcessor setMusicStatusProcessor) {
            this.val$target = setMusicStatusProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624081:
                    this.val$target.setMusicStatus((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.92 */
    class AnonymousClass92 implements Subscriber {
        final /* synthetic */ GetSearchMusicProcessor val$target;

        AnonymousClass92(GetSearchMusicProcessor getSearchMusicProcessor) {
            this.val$target = getSearchMusicProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624068:
                    this.val$target.getSearch((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.93 */
    class AnonymousClass93 implements Subscriber {
        final /* synthetic */ GetCustomTrackProcessor val$target;

        AnonymousClass93(GetCustomTrackProcessor getCustomTrackProcessor) {
            this.val$target = getCustomTrackProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624024:
                    this.val$target.customTrack((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.94 */
    class AnonymousClass94 implements Subscriber {
        final /* synthetic */ GetTunersProcessor val$target;

        AnonymousClass94(GetTunersProcessor getTunersProcessor) {
            this.val$target = getTunersProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624072:
                    this.val$target.getTuners((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.95 */
    class AnonymousClass95 implements Subscriber {
        final /* synthetic */ GetSearchArtistsProcessor val$target;

        AnonymousClass95(GetSearchArtistsProcessor getSearchArtistsProcessor) {
            this.val$target = getSearchArtistsProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624067:
                    this.val$target.getSearchArtists((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.96 */
    class AnonymousClass96 implements Subscriber {
        final /* synthetic */ GetAlbumTracksProcessor val$target;

        AnonymousClass96(GetAlbumTracksProcessor getAlbumTracksProcessor) {
            this.val$target = getAlbumTracksProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624043:
                    this.val$target.getAlbumTracks((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.97 */
    class AnonymousClass97 implements Subscriber {
        final /* synthetic */ GetCollectionInfoProcessor val$target;

        AnonymousClass97(GetCollectionInfoProcessor getCollectionInfoProcessor) {
            this.val$target = getCollectionInfoProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624049:
                    this.val$target.getCollectionInfo((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.98 */
    class AnonymousClass98 implements Subscriber {
        final /* synthetic */ GetHistoryMusicProcessor val$target;

        AnonymousClass98(GetHistoryMusicProcessor getHistoryMusicProcessor) {
            this.val$target = getHistoryMusicProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624056:
                    this.val$target.getHistoryMusic((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.99 */
    class AnonymousClass99 implements Subscriber {
        final /* synthetic */ UnSubscribeMusicCollectionProcessor val$target;

        AnonymousClass99(UnSubscribeMusicCollectionProcessor unSubscribeMusicCollectionProcessor) {
            this.val$target = unSubscribeMusicCollectionProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624083:
                    this.val$target.unSubscribeCollection((BusEvent) event);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.bus.GlobalBusRegistrar.9 */
    class C02469 implements Subscriber {
        final /* synthetic */ ValidatePlaceProcessor val$target;

        C02469(ValidatePlaceProcessor validatePlaceProcessor) {
            this.val$target = validatePlaceProcessor;
        }

        public final void consume(@AnyRes int kind, @NonNull Object event) {
            switch (kind) {
                case 2131624037:
                    this.val$target.validatePlace((BusEvent) event);
                default:
            }
        }
    }

    static {
        INSTANCE = new GlobalBusRegistrar();
    }

    private GlobalBusRegistrar() {
    }

    public final void register(@NonNull Bus bus, @NonNull Starter target) {
        Subscriber proxy = new C02391(target);
        bus.subscribeProxy(2131624108, proxy, target, 0);
        bus.subscribeProxy(2131624086, proxy, target, 0);
    }

    public final void register(@NonNull Bus bus, @NonNull SetStatusProcessor target) {
        bus.subscribeProxy(2131624116, new C02402(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetRecommendedFriendsProcessor target) {
        bus.subscribeProxy(2131624065, new C02413(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull PymkProcessor target) {
        Subscriber proxy = new C02424(target);
        bus.subscribeProxy(2131623988, proxy, target, 2131623944);
        bus.subscribeProxy(2131623989, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GuestProcessor target) {
        Subscriber proxy = new C02435(target);
        bus.subscribeProxy(2131624096, proxy, target, 2131623944);
        bus.subscribeProxy(2131623984, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull SearchQuickProcessor target) {
        bus.subscribeProxy(2131624113, new C02446(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetStatusProcessor target) {
        bus.subscribeProxy(2131624070, new C02458(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ValidatePlaceProcessor target) {
        bus.subscribeProxy(2131624037, new C02469(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ComplaintPlaceProcessor target) {
        bus.subscribeProxy(2131624023, new AnonymousClass10(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPlacesProcessor target) {
        bus.subscribeProxy(2131624029, new AnonymousClass11(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ReverseGeocodeProcessor target) {
        bus.subscribeProxy(2131624035, new AnonymousClass12(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetCategoriesProcessor target) {
        bus.subscribeProxy(2131624028, new AnonymousClass13(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull AppPollProcessor target) {
        Subscriber proxy = new AnonymousClass14(target);
        bus.subscribeProxy(2131623983, proxy, target, 2131623944);
        bus.subscribeProxy(2131624099, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull UsersProcessor target) {
        Subscriber proxy = new AnonymousClass15(target);
        bus.subscribeProxy(2131624074, proxy, target, 2131623944);
        bus.subscribeProxy(2131624120, proxy, target, 2131623944);
        bus.subscribeProxy(2131624122, proxy, target, 2131623944);
        bus.subscribeProxy(2131624123, proxy, target, 2131623944);
        bus.subscribeProxy(2131624121, proxy, target, 2131623944);
        bus.subscribeProxy(2131623957, proxy, target, 2131623944);
        bus.subscribeProxy(2131624111, proxy, target, 2131623944);
        bus.subscribeProxy(2131624039, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull CurrentUserInfoProcessor target) {
        Subscriber proxy = new AnonymousClass16(target);
        bus.subscribeProxy(2131624051, proxy, target, 2131623944);
        bus.subscribeProxy(2131624052, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetStreamProcessor target) {
        bus.subscribeProxy(2131624017, new AnonymousClass17(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull StreamMiscProcessor target) {
        bus.subscribeProxy(2131624107, new AnonymousClass18(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetFriendsProcessor target) {
        Subscriber proxy = new AnonymousClass19(target);
        bus.subscribeProxy(2131624119, proxy, target, 2131623944);
        bus.subscribeProxy(2131623986, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull MutualFriendsProcessor target) {
        bus.subscribeProxy(2131624092, new AnonymousClass20(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull FriendsFilterProcessor target) {
        bus.subscribeProxy(2131623981, new AnonymousClass21(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetSimilarMoviesProcessor target) {
        bus.subscribeProxy(2131624125, new AnonymousClass22(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull VideoProcessor target) {
        bus.subscribeProxy(2131624124, new AnonymousClass24(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull VideoLikeProcessor target) {
        Subscriber proxy = new AnonymousClass25(target);
        bus.subscribeProxy(2131624010, proxy, target, 2131623944);
        bus.subscribeProxy(2131624118, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ChangePasswordProcessor target) {
        bus.subscribeProxy(2131623949, new AnonymousClass26(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull AuthorizationSettingsProcessor target) {
        bus.subscribeProxy(2131623948, new AnonymousClass27(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull RegisterWithLibVerifyProcessor target) {
        bus.subscribeProxy(2131624095, new AnonymousClass28(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ConversationsProcessor target) {
        bus.subscribeProxy(2131623959, new AnonymousClass29(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull MessagesProcessor target) {
        Subscriber proxy = new AnonymousClass30(target);
        bus.subscribeProxy(2131623950, proxy, target, 2131623944);
        bus.subscribeProxy(2131623951, proxy, target, 2131623944);
        bus.subscribeProxy(2131623954, proxy, target, 2131623944);
        bus.subscribeProxy(2131623953, proxy, target, 2131623944);
        bus.subscribeProxy(2131623952, proxy, target, 2131623944);
        bus.subscribeProxy(2131623958, proxy, target, 2131623944);
        bus.subscribeProxy(2131623960, proxy, target, 2131623944);
        bus.subscribeProxy(2131624033, proxy, target, 2131623944);
        bus.subscribeProxy(2131624025, proxy, target, 2131623944);
        bus.subscribeProxy(2131624036, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull MessagesChunksProcessor target) {
        Subscriber proxy = new AnonymousClass31(target);
        bus.subscribeProxy(2131624031, proxy, target, 2131623945);
        bus.subscribeProxy(2131624034, proxy, target, 2131623945);
        bus.subscribeProxy(2131624032, proxy, target, 2131623944);
        bus.subscribeProxy(2131624030, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull MediaTopicGetByPhotoProcessor target) {
        bus.subscribeProxy(2131624091, new AnonymousClass32(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull MediaTopicEditTextProcessor target) {
        bus.subscribeProxy(2131624090, new AnonymousClass33(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull MediatopicProcessor target) {
        Subscriber proxy = new AnonymousClass34(target);
        bus.subscribeProxy(2131624018, proxy, target, 2131623944);
        bus.subscribeProxy(2131624019, proxy, target, 2131623944);
        bus.subscribeProxy(2131624020, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull StickersProcessor target) {
        bus.subscribeProxy(2131624103, new AnonymousClass35(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull MarkAsReadDiscussionsProcessor target) {
        bus.subscribeProxy(2131624079, new AnonymousClass36(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull DiscussionAddProcessor target) {
        bus.subscribeProxy(2131623963, new AnonymousClass37(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull DiscussionsMarkAsReadProcessor target) {
        Subscriber proxy = new AnonymousClass38(target);
        bus.subscribeProxy(2131623970, proxy, target, 2131623944);
        bus.subscribeProxy(2131623971, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull DiscussionChunksProcessor target) {
        Subscriber proxy = new AnonymousClass39(target);
        bus.subscribeProxy(2131623967, proxy, target, 2131623944);
        bus.subscribeProxy(2131623969, proxy, target, 2131623944);
        bus.subscribeProxy(2131623968, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull DiscussionProcessor target) {
        Subscriber proxy = new AnonymousClass40(target);
        bus.subscribeProxy(2131623964, proxy, target, 2131623944);
        bus.subscribeProxy(2131623972, proxy, target, 2131623944);
        bus.subscribeProxy(2131623966, proxy, target, 2131623944);
        bus.subscribeProxy(2131623965, proxy, target, 2131623944);
        bus.subscribeProxy(2131623975, proxy, target, 2131623944);
        bus.subscribeProxy(2131623973, proxy, target, 2131623944);
        bus.subscribeProxy(2131623974, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull SetAlbumMainPhotoProcessor target) {
        bus.subscribeProxy(2131624114, new AnonymousClass41(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPhotoAlbumsProcessor target) {
        bus.subscribeProxy(2131624002, new AnonymousClass42(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ImageUploadNotificationProcessor target) {
        Subscriber proxy = new AnonymousClass43(target);
        bus.subscribeProxy(2131624008, proxy, target, 2131623944);
        bus.subscribeProxy(2131624007, proxy, target, 2131623944);
        bus.subscribeProxy(2131624225, proxy, target, 2131623946);
    }

    public final void register(@NonNull Bus bus, @NonNull MarkPhotoSpamProcessor target) {
        bus.subscribeProxy(2131624089, new AnonymousClass44(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPhotosProcessor target) {
        bus.subscribeProxy(2131624005, new AnonymousClass45(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull DeletePhotoProcessor target) {
        bus.subscribeProxy(2131623977, new AnonymousClass46(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull LikePhotoProcessor target) {
        bus.subscribeProxy(2131624016, new AnonymousClass47(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPhotoAlbumProcessor target) {
        bus.subscribeProxy(2131624000, new AnonymousClass48(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull SetMainPhotoProcessor target) {
        bus.subscribeProxy(2131624115, new AnonymousClass49(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull EditPhotoAlbumProcessor target) {
        bus.subscribeProxy(2131623979, new AnonymousClass50(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPhotoTagsProcessor target) {
        bus.subscribeProxy(2131624004, new AnonymousClass51(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull LikePhotoAlbumProcessor target) {
        bus.subscribeProxy(2131624015, new AnonymousClass52(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull EditPhotoProcessor target) {
        bus.subscribeProxy(2131623980, new AnonymousClass53(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull DeletePhotoAlbumProcessor target) {
        bus.subscribeProxy(2131623976, new AnonymousClass54(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull MarkPhotoProcessor target) {
        bus.subscribeProxy(2131624088, new AnonymousClass55(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull CreatePhotoAlbumProcessor target) {
        bus.subscribeProxy(2131623961, new AnonymousClass56(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPhotoInfoProcessor target) {
        bus.subscribeProxy(2131624003, new AnonymousClass57(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPhotoAlbumsBatchProcessor target) {
        bus.subscribeProxy(2131624001, new AnonymousClass58(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetAlbumInfoBatchProcessor target) {
        bus.subscribeProxy(2131623998, new AnonymousClass59(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetViewInfoBatchProcessor target) {
        bus.subscribeProxy(2131624006, new AnonymousClass60(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetFullPhotoInfoProcessor target) {
        bus.subscribeProxy(2131623999, new AnonymousClass61(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull DeleteUserPhotoTagProcessor target) {
        bus.subscribeProxy(2131623978, new AnonymousClass62(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ImageUploadProcessor target) {
        bus.subscribeProxy(2131624084, new AnonymousClass63(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull StoreLastSuccessfulImageUploadTimeProcessor target) {
        bus.subscribeProxy(2131624225, new AnonymousClass64(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull RingtoneProcessor target) {
        bus.subscribeProxy(2131624098, new AnonymousClass65(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull RemoveOldDataProcessor target) {
        bus.subscribeProxy(2131624097, new AnonymousClass66(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull LikeProcessor target) {
        Subscriber proxy = new AnonymousClass67(target);
        bus.subscribeProxy(2131624009, proxy, target, 2131623944);
        bus.subscribeProxy(2131624117, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetEventsProcessor target) {
        Subscriber proxy = new AnonymousClass68(target);
        bus.subscribeProxy(2131624053, proxy, target, 2131623944);
        bus.subscribeProxy(2131623987, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ServicesSettingsProcessor target) {
        bus.subscribeProxy(2131624101, new AnonymousClass69(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull StartSettingsGetProcessor target) {
        bus.subscribeProxy(2131624102, new AnonymousClass70(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull MediaComposerSettingsProcessor target) {
        bus.subscribeProxy(2131624022, new AnonymousClass71(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetVideoCallParamsProcessor target) {
        bus.subscribeProxy(2131624048, new AnonymousClass72(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GcmRegisterProcessor target) {
        bus.subscribeProxy(2131623982, new AnonymousClass73(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull DeleteTrackProcessor target) {
        bus.subscribeProxy(2131624040, new AnonymousClass74(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetCollectionTracksProcessor target) {
        bus.subscribeProxy(2131624050, new AnonymousClass75(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetArtistInfoProcessor target) {
        bus.subscribeProxy(2131624045, new AnonymousClass76(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetAlbumsForArtistProcessor target) {
        bus.subscribeProxy(2131624044, new AnonymousClass77(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetMyMusicCollectionsProcessor target) {
        bus.subscribeProxy(2131624058, new AnonymousClass78(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPopCollectionTracksProcessor target) {
        bus.subscribeProxy(2131624062, new AnonymousClass79(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetSearchAlbumsProcessor target) {
        bus.subscribeProxy(2131624066, new AnonymousClass80(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPopMusicCollectionsProcessor target) {
        bus.subscribeProxy(2131624064, new AnonymousClass81(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetAlbumInfoProcessor target) {
        bus.subscribeProxy(2131624042, new AnonymousClass82(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPlayListInfoProcessor target) {
        bus.subscribeProxy(2131624060, new AnonymousClass83(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetMyMusicProcessor target) {
        bus.subscribeProxy(2131624057, new AnonymousClass84(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull StatusPlayMusicProcessor target) {
        bus.subscribeProxy(2131624087, new AnonymousClass85(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPlayTrackInfoProcessor target) {
        bus.subscribeProxy(2131624061, new AnonymousClass86(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetMyFriendsProcessor target) {
        bus.subscribeProxy(2131624059, new AnonymousClass87(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetArtistSimilarTracksProcessor target) {
        bus.subscribeProxy(2131624046, new AnonymousClass88(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetTunerTracksProcessor target) {
        bus.subscribeProxy(2131624073, new AnonymousClass89(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetUserMusicProcessor target) {
        bus.subscribeProxy(2131624075, new AnonymousClass90(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull SetMusicStatusProcessor target) {
        bus.subscribeProxy(2131624081, new AnonymousClass91(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetSearchMusicProcessor target) {
        bus.subscribeProxy(2131624068, new AnonymousClass92(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetCustomTrackProcessor target) {
        bus.subscribeProxy(2131624024, new AnonymousClass93(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetTunersProcessor target) {
        bus.subscribeProxy(2131624072, new AnonymousClass94(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetSearchArtistsProcessor target) {
        bus.subscribeProxy(2131624067, new AnonymousClass95(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetAlbumTracksProcessor target) {
        bus.subscribeProxy(2131624043, new AnonymousClass96(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetCollectionInfoProcessor target) {
        bus.subscribeProxy(2131624049, new AnonymousClass97(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetHistoryMusicProcessor target) {
        bus.subscribeProxy(2131624056, new AnonymousClass98(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull UnSubscribeMusicCollectionProcessor target) {
        bus.subscribeProxy(2131624083, new AnonymousClass99(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull AddTrackProcessor target) {
        bus.subscribeProxy(2131624038, new AnonymousClass100(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetUserMusicCollectionsProcessor target) {
        bus.subscribeProxy(2131624076, new AnonymousClass101(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull SubscribeMusicCollectionProcessor target) {
        bus.subscribeProxy(2131624082, new AnonymousClass102(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPopMusicProcessor target) {
        bus.subscribeProxy(2131624063, new AnonymousClass103(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull Play30MusicProcessor target) {
        bus.subscribeProxy(2131624080, new AnonymousClass104(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetRelevantProcessor target) {
        bus.subscribeProxy(2131624069, new AnonymousClass105(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetArtistTrackProcessor target) {
        bus.subscribeProxy(2131624047, new AnonymousClass106(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull NotificationProcessor target) {
        bus.subscribeProxy(2131624093, new AnonymousClass107(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull SendPresentProcessor target) {
        Subscriber proxy = new AnonymousClass108(target);
        bus.subscribeProxy(2131624013, proxy, target, 2131623944);
        bus.subscribeProxy(2131624100, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GetPresentsProcessor target) {
        bus.subscribeProxy(2131624012, new AnonymousClass109(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ReceivePresentProcessor target) {
        Subscriber proxy = new AnonymousClass110(target);
        bus.subscribeProxy(2131624014, proxy, target, 2131623944);
        bus.subscribeProxy(2131623947, proxy, target, 2131623944);
        bus.subscribeProxy(2131623962, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull GroupsProcessor target) {
        Subscriber proxy = new AnonymousClass111(target);
        bus.subscribeProxy(2131623994, proxy, target, 2131623944);
        bus.subscribeProxy(2131623995, proxy, target, 2131623944);
        bus.subscribeProxy(2131623996, proxy, target, 2131623944);
        bus.subscribeProxy(2131623997, proxy, target, 2131623944);
        bus.subscribeProxy(2131624105, proxy, target, 2131623944);
        bus.subscribeProxy(2131623956, proxy, target, 2131623944);
        bus.subscribeProxy(2131623992, proxy, target, 2131623944);
        bus.subscribeProxy(2131623993, proxy, target, 2131623944);
        bus.subscribeProxy(2131623990, proxy, target, 2131623944);
        bus.subscribeProxy(2131623955, proxy, target, 2131623944);
        bus.subscribeProxy(2131623991, proxy, target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull ExpireSessionProcessor target) {
        bus.subscribeProxy(2131624041, new AnonymousClass112(target), target, 2131623944);
    }

    public final void register(@NonNull Bus bus, @NonNull LogoutAllProcessor target) {
        bus.subscribeProxy(2131623985, new AnonymousClass113(target), target, 2131623944);
    }
}
