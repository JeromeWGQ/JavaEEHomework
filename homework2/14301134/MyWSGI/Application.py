def application(environ, start_response):
    start_response('200 OK', [('Content-Type', 'text/html')])
    if environ['PATH_INFO'][1:]:
        body = '<h1>Received text: %s</h1>' % environ['PATH_INFO'][1:]
    else:
        body = '<h1>This is the default static page.</h1>'

    return [body.encode('utf-8')]

