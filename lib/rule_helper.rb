module RuleHelper
  def basename(fname)
    File.basename(fname, File.extname(fname))
  end
  
  def sass_partial?(item)
    item.identifier =~ /stylesheets\/_/
  end
end

module Nanoc
  class RuleContext
    include RuleHelper
  end
end
