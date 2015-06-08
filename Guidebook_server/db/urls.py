from django.conf.urls import patterns, url

from db import views

urlpatterns = patterns('',
    url(r'^test$', views.test, name='test'),
)

