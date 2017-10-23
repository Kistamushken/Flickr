# Flickr

# <a name="notice" />IMPORTANT

In order for this app to work properly, please insert your Api Key into res/strings.xml

## <a name="general-overview" />General overview

This project aims to provide implementation of the [MVI architecture](http://hannesdorfmann.com/android/mosby3-mvi-1)

The app consists of one screen that shows photos. Upon entering the App,
you will be presented with recent photos form Flickr(`?method=flickr.photos.getRecent`).
Then you will be able to specify your own query and `?method=flickr.photos.search` will be triggered

## <a name="architecture-overview" />Architecture Overview
While it is far from pure, this project aims to provide implementation
of the [MVI architecture](http://hannesdorfmann.com/android/mosby3-mvi-1)

![Alt text](scheme.jpeg?raw=true)

Main screen is built with 3 main components: **View**, **Presenter** and a **UseCase**

**Responsibilities**
* **View**. View is a passive entity, it receives states to render and
provides `Presenter` with hot streams of events(e.g. load more event, query event, etc.)

* **Presenter**. `Presenter` is responsible for bridging business logic(i.e. `UseCase`)
with a view layer. It maps raw events from view into `ViewAction`s.
Upon receiving new `ViewState` from a `UseCase` it calls
`render` on the provided view to display the latest state.
In current implementation `Presenter` is also responsible
for managing subscriptions lifecycle.

* **UseCase**. UseCase takes desired `ViewAction` as an input and based
 on current state produces new state. UseCase represents business logic,
 and it is responsible for managing state of the system.

 * **Activity**. Activity is used as a DI container, it also provides
 presenter with lifecycle events


## <a name="short_cuts" />Shortcuts

While current implementation allows for easy testing/debugging and extensibility,
as it has been mentioned before, it is far from _pure_, as there were some shortcuts taken

* **UseCase**. `reduce` function is not pure. All in all, **UseCase**
implementation could be more reactive
* **Lifecycle**. Activity calls lifecycle methods on a `Presenter` in a
 declarative way, this could be done in a reactive way
* **Adapter**. Generally, I would prefer something like [Konveyor](https://github.com/avito-tech/Konveyor)
 to work with RecyclerView. But it seemed like it would be on overkill.
* **Lack of UI tests**
* **Dagger modules** While not necessary a shortcut, Dagger modules are written
in Java in order to take advantage of static `@Provides` methods