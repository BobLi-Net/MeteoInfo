import sys
import os

toolbox_path = 'D:/MyProgram/Java/MeteoInfoDev/toolbox'
sys.path.append(toolbox_path)
sys.path.append(os.path.join(toolbox_path, 'miml_dev'))
sys.path.append(os.path.join(toolbox_path, 'emips_dev'))
sys.path.append(os.path.join(toolbox_path, 'IMEP'))

mipylib.migl.mifolder = 'D:/MyProgram/Distribution/Java/MeteoInfo/MeteoInfo'