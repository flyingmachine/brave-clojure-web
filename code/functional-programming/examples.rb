class GlamourShotCaption
  attribute_reader :text
  def initialize(text)
    @text = text
    clean!
    exclamate!
  end

  def save
    File.open("read_and_feel_giddy.txt", "w+"){ |f|
      f.puts text
    }
  end

  private
  def clean!
    text.trim!
  end

  def exclamate!
    text += "!!!!"
  end
end

best = GlamourShotCaption.new("My boa constrictor is so
sassy lol!  ")
