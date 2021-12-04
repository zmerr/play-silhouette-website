A Play Silhouette sample website built with the Silhouette Seed template originally found here https://github.com/mohiva/play-silhouette-seed

For further information about this project including accessing the tutorial please visit the original template above.

This app uses the honeycomb-cheesecake version of Play Silhoutte https://github.com/honeycomb-cheesecake/play-silhouette.

The website doesn't have any content. For adding them, for adding them, you can either edit the Twirl files or use an integration of Play with frameworks such as (React)[https://medium.com/@yohan.gz/react-with-play-framework-2-6-x-a6e15c0b7bd] or (Vue)[https://github.com/gbogard/play-vue-webpack].

These are the features:

-  connectong to PostgreSQL through HikariCP connection pool,
-  signing up, logging in, 
-  filling in contact and support forms, 
-  downloading a file after login
-  sending emails to users and support team with Play Mailer

You need to create the database tables and add the required variables mentioned in application.conf to the environment to make it run. This includes the support's email login information, the Play's application secret, and database connection parameters.
