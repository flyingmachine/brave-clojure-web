---
title: Whoops Server
kind: page
toc: true
---

# Whoops Server

The Whoops server is a Rails engine which records logs and provides an
interface to filter, search, and view them. Below are its features and
how it compares to Hoptoad:

[![Dash](/assets/images/screens/dash.png)](/assets/images/screens/dash.png)

## Features

### Log Arbitrary Events

With Airbrake, you only log exceptions. With Whoops, it's up to you to
tell the Whoops server what you're logging, be it an exception,
notification, warning, or whatever. Internally, `Whoops::EventGroup`
uses the `event_type` field to store the event type. You can filter on
this field when viewing a listing of all events.

### Log Arbitrary Details

With many logging systems, the fields which you can log are
pre-defined. They also reflect an assumption that your error happened
within the context of handling an HTTP request. Whoops uses mongodb as
its database and this allows you to log whatever details you want. For
example, you could log the following:

``` ruby
{
  :start_time => 1310748754,
  :end_time   => 1310949834,
  :users_imported => [
    { :id => 413, :succeeded => false },
    { :id => 835, :succeeded => true },
    { :id => 894, :succeeded => true },
    { :id => 124, :succeeded => true },
  ],
}
```

This gets stored as-is in Whoops. You can also search these details, as explained below:

### Search Event Details

As far I know, you can't search Airbrake or Errbit. Graylog2 provides
search. Whoops provides two kinds of search: event detail search
within for all events within an EventGroup, and keywords search across
all events and event groups.

Below is example text you would write to search event details, and
below that is essentially the ruby code that ends up getting run by
the server.

```
details.current_user_id#in [3, 54, 532]      <1>
details.num_failures#gt 3                    <2>
details.current_user.first_name Voldemort    <3>
message !r/(yesterday|today)/                <4>
```

1. `Event.where( {:"details.current_user_id".in => [3, 54, 532]} )`
2. `Event.where( {:"details.num_failure".gt => 3} )`
3. `Event.where( {:"details.current_user.first_name" => "Voldemort"} )`
4. `Event.where( {:message => /(yesterday|today/)} )` Note that regular expressions must start with !r.
  
The general format is `key[#mongoid_method] query` . As you can see,
`query` can be a string, number, regex, or array of these values.
Hashes are allowed too. If you're not familiar with querying mongo,
you can [read more in the
mongodb docs](http://www.mongodb.org/display/DOCS/Querying). The
[Mongoid](http://two.mongoid.org/docs/querying/criteria.html#where) docs are
useful as well.

### Extend the App

Since Whoops is a Rails engine, you can make changes to your base
rails app without worrying about merge difficulties when you upgrade
Whoops. For example, you could add basic HTTP authentication.

### No Users or Projects

In Airbrake, errors are assigned to projects, and access to projects is
given to users. In Whoops, there are no users, so it's not necessary
to manage access rights or even to log in. Additionally, there is no
Project model within the code or database. Instead, each EventGroup
has a `service` field which you can filter on. Services can be
namespaced, so that if you have the services "godzilla.web" and
"godzilla.background", you can set a filter to show events related to
either service or both.

Note that you can add users and/or authentication to the base rails
app if you really want to.

### Notifications

Since Whoops doesn't have users, email notification of events is
handled by entering an email address along with a newline-separated
list of services to receive notifications for.


[![Dash](/assets/images/screens/notification-rules.png)](/assets/images/screens/notification-rules.png)

Notifications are sent in two circumstances:

* A new kind of event is received
* An event is received for an archived event group

You must set the ActionMailer settings in your base Rails app in order
to send notifications. Additionally, you must set the "from" email
address with `Rails.application.config.whoops_sender`

### Archival

You can archive a specific event group when viewing its details page.
This prevents the event group from showing up in the event group list.

You can view archived event groups by appending `show_archived=true`
to the event group url. For example:
`http://localhost:3000/event_groups?show_archived=true` . A more
elegant way to do this will be implemented in the future.

If a new event comes in for an event group after the event group is
archived, a notification will be sent.

### You Manage the Rails App

If you use Whoops you'll have to manage the Rails app yourself. You'll
have to set up mongodb and all that. Heroku has a
[great mongodb addon](http://addons.heroku.com/mongolab) that gives you
240mb of space for free. Hoptoad doesn't require you to host or manage
anything.

Since Whoops is self-hosted, you can set it up behind your firewall.

## Setup

View the [home page](/) for instructions on quickly setting up a
Heroku server.

1. create a new rails app
2. add `gem "whoops"` to your Gemfile
3. run `bundle`
4. add [`config/mongoid.yml`](http://two.mongoid.org/docs/installation/configuration.html)
5. *optional* add `root :to => "event_groups#index"` to your routes file to make the event group listing your home page
6. add [loggers](https://github.com/flyingmachine/whoops_logger) to the code you want to monitor
7. ensure that your `config/application.rb` file looks something like
the following:

``` ruby
# make sure that you're not requiring active record
require "action_controller/railtie"
require "action_view/railtie"
require "action_mailer/railtie"
require "sprockets/railtie"

if defined?(Bundler)
  Bundler.require(*Rails.groups(:assets => %w(development test)))
end

module WhoopsServer
  class Application < Rails::Application
    config.encoding = "utf-8"
    config.assets.enabled = true
    config.filter_parameters += [:password]
    # optional - only for sending email notifications
    config.whoops_sender = "whoops@yourdomain.com"
  end
end
```

## Usage

### Filtering

[![Dash](/assets/images/screens/dash.png)](/assets/images/screens/dash.png)

When viewing the Event Group list, you can filter by service, environment, and event type.

When you set a filter, its value is stored in a session and won't be changed until you click "reset". This is so that you won't lose your filter after, for example, viewing a specific event.
