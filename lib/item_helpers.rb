include Nanoc::Helpers::Rendering
include Nanoc::Helpers::LinkTo

class Chapters
  class << self
    def chapter_ids_ordered
      { "cftbat" => %w{
  foreword
  acknowledgements
  introduction
  getting-started
  basic-emacs
  do-things
  core-functions-in-depth
  functional-programming
  organization
  read-and-eval
  writing-macros
  concurrency
  zombie-metaphysics
  core-async
  java
  multimethods-records-protocols
  appendix-a
  appendix-b
  afterword
        },
        "deploy" => %w{
        preface
        intro
        set-up-a-server-and-deploy-a-clojure-app-to-it
        ansible-tutorial
        sweet-tooth-deep-dive
        },
        "reducers" => %w{
        intro
        know-your-reducers
        appendix-x
        references
        }
      }
    end
  end
end

def chapter_order(item, book)
  m = /\/([^\/]*)\/$/.match(item.identifier)[1]
  Chapters.chapter_ids_ordered[book].index(m)
end

def chapters(item)
  book = item[:book]
  @chapters ||= @items.select{|i|
    i[:kind] == 'chapter' && !i[:draft] && book && i[:book] == book
  }.sort{ |a, b|
    chapter_order(a, book) <=> chapter_order(b, book)
  }
end


