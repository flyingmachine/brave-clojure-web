require 'RedCloth'
module CustomTags  
  #TODO - allow method specification instead of line numbers

  # Usage
  # Note: this currently requires pygments
  # 1. Create the cirectory content/assets/source
  # 2. add a file to content/assets/source
  # 3. Add something like one of the following four examples to your textile file:
  # source. ruby/aikidoka.rb         # show whole file
  # source. ruby/aikidoka.rb:10      # show whole file, highlight line 10
  # source. ruby/aikidoka.rb 5-20    # show lines 5-20
  # source. ruby/aikidoka.rb:10 5-20 # show lines 5-20, highline line 10
  
  def source(opts)    
    local_path, highlighted, range = /([^ :]*)(?::(\d+))?(?: (\d+-\d+))?/.match(opts[:text])[1..3]
    
    code = File.readlines(File.join(File.expand_path(File.dirname(__FILE__)), "..", "content", "assets", "source", local_path))

    # TODO support more languages
    langs = {"rb" => "ruby", "clj" => "clojure"}
    lang = langs[File.extname(local_path).gsub(".", "")]
    
    if range
      start, finish = range.split("-").collect{|i| i.to_i}
      code = code[(start-1)..(finish-1)]
    end
    start ||= 1
    
    indentation_level = /^ */.match(code[0])[0].size
    code.collect!{|l| l.sub(/^ {#{indentation_level}}/, '')} #remove indendation
    code = code.join
    
    html = "<div class='attachment-path source'>"
    html << "<a href='/assets/source/#{local_path}'>#{local_path}</a></div><div class='code pygments'>"

    html << IO.popen("pygmentize -O linenos=table,linenostart=#{start} -f html -l #{lang}", 'a+') do |pygmentize|
      pygmentize.puts code
      pygmentize.close_write
      result = ""
      while (line = pygmentize.gets)
        result << line
      end
      result
    end

    html << "</div>"
    
    html
  end
  
  # Ignore notes text; just want to keep it on page for later development
  def notes(opts)
    return ""
  end
end
RedCloth::Formatters::HTML.send(:include, CustomTags)
