from flask import Flask
from flask_restful import Resource, Api

app = Flask(__name__)
api = Api(app)

class Demo(Resource):
    def get(self):
        pass # Must coordinate w/ DB before any logic can be implemented
    
    def post(self, message):
        pass


api.add_resource(Demo, '/')

if __name__ == '__main__':
    app.run()
