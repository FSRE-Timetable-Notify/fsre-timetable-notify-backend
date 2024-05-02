# FSRE Timetable Notifier Backend

Backend for the FSRE Timetable Notifier project. Made in Spring Boot.

# App Flow

## Client

The client generates a unique FCM token which gets sent to the backend. Then, the client can pick multiple study programs which they want to subscribe to. These subscriptions get persisted on the backend. Then, whenever the backend runs a cron job which refreshes the timetables for the specific study program the current user has subscribed to, the client will receive a notification. These notifications get persisted locally.

The client can be in a few different states:

1. First run:
   - The FCM API has been initialized for the first time - the background listener gets registered.
   - The client registers their device by sending their unique FCM token to the backend, so that they can receive notifications for any subscriptions they have.
   - The client doesn't have any subscriptions registered, so they won't receive any notifications.
   - The client creates the necessary data holders locally for things like the notification history and theme (these will have some default values set, eg. the notification history will be an empty array and the theme will be "system").
2. Cold starts:
   - The client reads data from the offline storage (timetable database, subscriptions, notification history, theme, etc.) and updates the BLoC state accordingly, thus updating the UI.
   - The client syncs its timetable database and user subscriptions data with the backend by sending a request and updating the local state and storage.
   - The foreground listener gets connected so that messages update the BLoC state and thus the UI.
3. Hot starts:
   - The client detects a "hot start" (app comes to foreground from a "suspended" state), and then the notification history is read from the local storage which updates the BLoC state accordingly, and thus updating the UI. This is in case the client got a background notification while it was suspended, since when the app opens, the initialization logic doesn't run because this is a hot start. This ensures that the notification history stays up to date to the actual notifications received.
4. Client receives notification:
   - Whether the client is in the foreground or the background, the notification will be appended to the notification history list in the local storage. This is done using a separate local storage service (called a repository in Flutter BLoC).
   - If the client is in the foreground, then the background listener won't get triggered, but the foreground listener will. As well as storing the notification in the local notification history (same as the background listener, this is why the local storage is a separate service, so we don't repeat ourselves), the foreground listener also updates the local BLoC state so the UI gets the correct state, similar to the way the Hot start section works.

## Style rules

1. Use Java 22 records for input DTOs (request objects).
2. Do not use custom transformation logic for input DTOs. Instead use ObjectMapper or manually create POJO instances. This is to make sure the OpenAPI documentation for the request bodies is correct.
3. Don't use records for response objects. Instead, return POJOs created manually with the help of Lombok's @Data, or return JPA entities directly.
4. If you need special transformation logic (eg. custom de/serialization logic or hiding sensitive fields from JPA entities), use @JsonComponent to annotate your custom Jackson de/serializer in the `transformers` package.
5. Use POJOs for JPA entities. Do not use Lombok's @Data with them, as it can cause issues with Hibernate. Instead, use @Getter and @Setter.
