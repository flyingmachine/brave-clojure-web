include Nanoc::Helpers::LinkTo

def chapters
  @items.select{|i| i[:kind] == 'documentation' && !i[:draft]}.sort{ |a, b|
    a.identifier[/^\d+/].to_i <=> b.identifier[/^\d+/].to_i 
  }
end
