---
title: Whoops Logger
kind: page
toc: true
---

# Whoops Logger

Use Whoops Logger to send log messages to a Whoops server.

## Rails Gem

Please note that there's a
[Rails Gem](https://github.com/flyingmachine/whoops_rails_logger) which
simplifies usage in two ways:

1. You don't have to specify the location of a whoops config file; it
defaults to config/whoops_logger.yml
2. It includes an exception logger which will handle all exceptions
within the context of a controller action.

Below are instructions for adding additional logging strategies and
using the "bare" Ruby client.

## Whoops Logger Setup

Add `whoops_logger` to your Gemfile

Add `WhoopsLogger.config.set(config_path)` to your project, where `config_path` is a path to a YAML file. The YAML file takes the following options:

``` ruby
:host
:http_open_timeout
:http_read_timeout
:port
:protocol
:proxy_host
:proxy_pass
:proxy_port
:proxy_user
:secure
```

You can also use pass a Hash to `WhoopsLogger.config.set` instead of a path to a YAML file.

## Usage

Whoops Logger sends Messages to Whoops. Messages are created with Strategies. Below is the basic strategy found in `lib/whoops_logger/basic.rb`:

``` ruby
strategy = WhoopsLogger::Strategy.new("default::basic")

strategy.add_message_builder(:use_basic_hash) do |message, raw_data|
  message.event_type             = raw_data[:event_type]
  message.service                = raw_data[:service]
  message.environment            = raw_data[:environment]
  message.message                = raw_data[:message]
  message.event_group_identifier = raw_data[:event_group_identifier]
  message.event_time             = raw_data[:event_time] if raw_data[:event_time]
  message.details                = raw_data[:details]
end
```

To use this strategy, you would call

``` ruby
WhoopsLogger.log("default::basic", {
  :event_type             => "your_event_type",
  :service                => "your_service_name",
  :environment            => "development",
  :message                => "String to Show in Whoops Event List",
  :event_group_identifier => "String used to assign related events to a group",
  :event_time             => Time.now # Defaults to now, so you can leave this out
  :details                => "A string, hash, or array of arbitrary data"
})
```

You can create as many strategies as you need. For example, in a Rails
app, you could use a strategy for logging exceptions which occur
during a controller action (in fact
[there's a gem for that](https://github.com/flyingmachine/whoops_rails_logger)).
You could use a separate strategy for logging exceptions which occur
during a background job. With controller actions, you care about
params, sessions, and that data. That data isn't even present in
background jobs, so it makes sense to use different strategies.

### Message Builders

Each strategy consists of one or more message builders. The message builders are called in the order in which they are defined.

Internally, each Strategy stores its message builders in the array `message_builders`, and it's possible to modify that array directly if you want. For example, you might want to modify a Strategy provided by a library.

The method `add_message_builder` is provided for convenience. Below is an example of `add_message_builder` taken from the [Whoops Rails Logger](https://github.com/flyingmachine/whoops_rails_logger):

``` ruby
# It's not necessary to break up the strategy into 3 message builders,
# but it could help to compartmentalize related portions of message building
strategy.add_message_builder(:basic_details) do |message, raw_data|
  message.service     = self.service
  message.environment = self.environment
  message.event_type  = "exception"
  message.message     = raw_data[:exception].message
  message.event_time  = Time.now
end

strategy.add_message_builder(:details) do |message, raw_data|
  exception = raw_data[:exception]
  rack_env  = raw_data[:rack_env]
  
  details = {}
  details[:backtrace] = exception.backtrace.collect{ |line|
    line.sub(/^#{ENV['GEM_HOME']}/, '$GEM_HOME').sub(/^#{Rails.root}/, '$Rails.root')
  }

  details[:http_host]      = rack_env["HTTP_HOST"]        
  details[:params]         = rack_env["action_dispatch.request.parameters"]
  details[:controller]     = details[:params][:controller] if details[:params]
  details[:action]         = details[:params][:action]     if details[:params]
  details[:query_string]   = rack_env["QUERY_STRING"]
  details[:remote_addr]    = rack_env["REMOTE_ADDR"]
  details[:request_method] = rack_env["REQUEST_METHOD"]
  details[:server_name]    = rack_env["SERVER_NAME"]
  details[:session]        = rack_env["rack.session"]
  details[:env]            = ENV.to_hash
  message.details          = details
end

strategy.add_message_builder(:create_event_group_identifier) do |message, raw_data|
  identifier = "#{message.details[:controller]}##{message.details[:action]}"
  identifier << raw_data[:exception].backtrace.collect{|b| b.gsub(/:in.*/, "")}.join("\n")
  message.event_group_identifier = Digest::SHA1.hexdigest(identifier)
end

strategy.add_message_builder(:basic_details) do |message, raw_data|
  message.service     = self.service
  message.environment = self.environment
  message.event_type  = "exception"
  message.message     = raw_data[:exception].message
  message.event_time  = Time.now
end
```

There's a bit more about message builders in the `WhoopsLogger::Strategy` documentation.

### Ignore Criteria

Sometimes you want to ignore a message instead of sending it off to
whoops. For example, you might not want to log "Record Not Found"
exceptions in Rails. If any of the ignore criteria evaluate to true,
then the message is ignored. Below is an example:

``` ruby
strategy.add_ignore_criteria(:ignore_record_not_found) do |message|
  message.message == "Record Not Found"
end

strategy.add_ignore_criteria(:ignore_dev_environment) do |message|
 message.environment == "development"
end
```
