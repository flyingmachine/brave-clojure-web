include Nanoc::Helpers::LinkTo

def chapters
  @chapters ||= @items.select{|i| i[:kind] == 'documentation' && !i[:draft]}.sort{ |a, b|
    /\d+/.match(a.identifier)[0].to_i <=> /\d+/.match(b.identifier)[0].to_i
  }
end
