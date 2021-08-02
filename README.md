# Versatile
robot for Robocode

## Design
### Gun
Uses multiple guns and maintains stats during the battle to adapt to the ennemy (hence the name)

The guns are:
- HoT
- linear
- dynamic segmentation (collects hit statistics and builds a binary search tree by dynamically splitting nodes according to the most effective dimension)

the last one is instanciated several times with different hyper parameters

### Movement
The bot uses anti gravity movement, with anti gravity on walls, corners, the ennemy and the center, as well as on where the ennemy bullets are believed to be

## Rankings
Used to be ranked ~120 in the 1v1 roborumble (http://literumble.appspot.com/Rankings?game=roborumble)

## Demo
![Versatile](https://user-images.githubusercontent.com/5497622/127932968-00062ac4-b040-4133-b2fa-bd543cee22fb.gif)

![VersatileDebug](https://user-images.githubusercontent.com/5497622/127935437-f7d19ab7-aada-44bc-8b20-a8f8b1116273.gif)
