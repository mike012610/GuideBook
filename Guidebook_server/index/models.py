from django.db import models

# Create your models here.
class Test(models.Model):
	text = models.CharField(max_length=20)
	value = models.IntegerField(default=0)
