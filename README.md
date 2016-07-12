# Pop Movies
This is the final project that students are required to do in
[Developing Android Apps (Udacity)](https://www.udacity.com/course/developing-android-apps--ud853). The
project requirements and mockups can be found [here](https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true#h.7sxo8jefdfll).
The criteria for passing this project can be found [here](https://review.udacity.com/#!/rubrics/67/view)
and [here](http://udacity.github.io/android-nanodegree-guidelines/core.html).

### Instructions
Add `movieDBApiKey="API-KEY"` to `~/.gradle/gradle.properties`. This can be found at www.themoviedb.org

### Stage 1 Tasks
- See a grid view of movie posters upon launch
- Change sort order by most popular or top rated.
- Tap a movie poster to transition to a details screen with
    - original title
    - movie poser image thumbnail
    - plot synopsis
    - user rating
    - release date

### Stage 2 Tasks
- View and play trailers (/movie/{id}/videos)
- Read reviews of a selected movie (/movie/{id}/reviews).
- Mark a movie as a favorite in the details view by tapping a
button (star). This is for a local movies collection.
- View favorites collection (additional pivot in sort spinner).
- Optimize app experience for tablets.
- Don't allow the app to crash upon rotation.

### Useful links
- [Parcelable vs Serializable](http://www.developerphil.com/parcelable-vs-serializable/)
- [Custom Array Adapter - Parcelable](https://github.com/udacity/android-custom-arrayadapter/tree/parcelable)
- [Custom Array Adapter - Grid View](https://github.com/udacity/android-custom-arrayadapter/tree/gridview)
- [Recreating an Activity](https://developer.android.com/training/basics/activity-lifecycle/recreating.html)
- [Content Providers Example](https://github.com/udacity/android-content-provider)
- [Setho - Debugging tool](http://facebook.github.io/stetho/)
- [Setho Tutorial](https://www.youtube.com/watch?v=iyXpdkqBsG8)
- [Recycler View Example](https://github.com/chrisbanes/cheesesquare)
- [Activity Lifecycle Example](https://github.com/udacity/android-lifecycle)
- [Schematic Content Provider Library](https://github.com/SimonVT/schematic)
- [Schematic Example](https://github.com/schordas/SchematicPlanets/tree/master)
- [CursorRecyclerView Adapter Gist](https://gist.github.com/skyfishjy/443b7448f59be978bc59)


