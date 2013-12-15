include Nanoc::Helpers::LinkTo

def chapter_order(item)
  chapter_ids_ordered = %w{
home
getting-started
basic-emacs
using-emacs-with-clojure
language-fundamentals-overview
do-things
core-functions-in-depth
functional-programming
organization
read-and-eval
writing-macros
upcoming
debugging
about
  }
  chapter_ids_ordered.index(item.identifier.gsub("/", ""))
end

def chapters
  @chapters ||= @items.select{|i| i[:kind] == 'documentation' && !i[:draft]}.sort{ |a, b|
    chapter_order(a) <=> chapter_order(b)
  }
end
