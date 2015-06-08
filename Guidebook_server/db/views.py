from django.shortcuts import render
from django.http import HttpResponse

# Create your views here.

def test(request):
	params = request.POST
	print "connect success"
	print params
	return HttpResponse("connect success")
