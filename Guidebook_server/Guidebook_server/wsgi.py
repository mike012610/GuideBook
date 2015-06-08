"""
WSGI config for Guidebook_server project.

It exposes the WSGI callable as a module-level variable named ``application``.

For more information on this file, see
https://docs.djangoproject.com/en/1.8/howto/deployment/wsgi/
"""

import os,sys

from django.core.wsgi import get_wsgi_application

sys.path.append('/home/mike012610/course/LBS/GuideBook/Guidebook_server')
os.environ.setdefault("DJANGO_SETTINGS_MODULE", "Guidebook_server.settings")

application = get_wsgi_application()
