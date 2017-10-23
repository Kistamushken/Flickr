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

License
-------
The MIT License (MIT)

Copyright (c) 2017 Filipp Uvarov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.