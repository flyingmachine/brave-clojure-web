class TocFilter < Nanoc::Filter
  identifier :toc
  type :text

  def run(content, params ={})
    NokogiriTOC.run(content, :content_selector => ".content", :toc_selector => "#toc.nav li.active-section")
  end
end

class NokogiriTOC
  def self.level_text
    [@level["h2"], @level["h3"], @level["h4"]].join(".").gsub(/\.0/, "") + "."
  end

  def self.to_anchor(content)
    content.gsub(/\W/, "_")
  end
  
  def self.run(html, options = {})
    options[:content_selector] ||= "body"

    doc = Nokogiri::HTML(html)
    return unless doc.at_css(options[:toc_selector])
    
    toc_data = []
    
    @level = {"h2" => 0, "h3" => 0, "h4" => 0}
    selector = @level.keys.map{|h| Nokogiri::CSS.xpath_for("#{options[:content_selector]} #{h}")}.join("|")

    current_heading = nil
    
    doc.xpath(selector).each do |node|
      current_heading = node.name
      @level[node.name] += 1

      @level["h3"] = 0 if node.name == "h2"
      @level["h4"] = 0 if node.name == "h2" || node.name == "h3"
        
      node.content = level_text + " " + node.content

      node["id"] = to_anchor(node.content)

      data = {:t => node.content, :c => []}
      parent = case node.name
      when "h2" then toc_data
      when "h3" then toc_data.last[:c]
      when "h4" then toc_data.last[:c].last[:c]
      end
      parent << data
    end

    toc = doc.create_element("ol")
    build_toc(toc, toc_data)

    doc.at_css(options[:toc_selector]).add_child(toc)
    doc.to_html
  end

  def self.build_toc(toc, data)
    data.each do |item|
      li = toc.document.create_element("li")
      li.add_child(li.document.create_element("a", item[:t], :href => "##{to_anchor(item[:t])}"))
      unless item[:c].empty?
        build_toc(li.add_child(li.document.create_element("ol")), item[:c])
      end
      toc.add_child(li)
    end
    toc
  end
end

