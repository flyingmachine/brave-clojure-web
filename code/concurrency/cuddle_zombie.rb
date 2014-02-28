class CuddleZombie
  attr_reader :cuddle_hunger_level, :percent_deteriorated

  def initialize(cuddle_hunger_level = 1, percent_deteriorated = 0)
    self.cuddle_hunger_level = cuddle_hunger_level
    self.percent_deteriorated = percent_deteriorated
  end

  def increase_hunger(x)
    self.cuddle_hunger_level += x
  end

  def shuffle_speed
    cuddle_hunger_level * (100 - percent_deteriorated)
  end
end
