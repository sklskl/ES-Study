import sys

import pygame

from alien_invasion.settings import Settings


class AlienInvasion:
    """管理游戏资源和行为的类"""

    def __init__(self):
        """初始化游戏并创建游戏资源"""
        pygame.init()
        # self.screen = pygame.display.set_mode((1200, 800))
        # pygame.display.set_caption("Alien Invasion")
        # self.bg_color = (230, 230, 230)
        self.settings = Settings()
        self.screen = pygame.display.set_mode(self.settings.screen_width, self.settings.screen_height)
        pygame.display.set_caption("Alien Invasion")

    def run_game(self):
        """开始游戏的主循环"""
        while True:
            # 监听键盘喝鼠标事件
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    sys.exit()

            self.screen.fill(self.settings.bg_color)

            # 让最近绘制的屏幕可见
            pygame.display.flip()


if __name__ == 'main':
    ai = AlienInvasion
    ai.run_game()
