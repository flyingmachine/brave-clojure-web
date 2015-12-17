include Nanoc::Helpers::Rendering
include Nanoc::Helpers::LinkTo

def chapter_order(item)
  chapter_ids_ordered = %w{
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
  }

  m = /\/cftbat\/(.*)\//.match(item.identifier)[1]
  chapter_ids_ordered.index(m)
end

def chapters
  @chapters ||= @items.select{|i| i[:kind] == 'chapter' && !i[:draft]}.sort{ |a, b|
    chapter_order(a) <=> chapter_order(b)
  }
end


