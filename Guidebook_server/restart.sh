#!/bin/sh
python manage.py collectstatic
sudo service apache2 restart
