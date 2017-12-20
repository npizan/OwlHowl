from flask import Flask
from flask_restful import Resource, Api, reqparse, abort
from db_funcs import getMessages, insertMessage, changeRating
from decimal import Decimal
import sys

app = Flask(__name__)
api = Api(app)

parser = reqparse.RequestParser()
parser.add_argument('message', required=False)
parser.add_argument('lat', type=Decimal, required=False)
parser.add_argument('lng', type=Decimal, required=False)
parser.add_argument('deviceName', required=False)
parser.add_argument('ratingChanges', required=False)

class Message(Resource):
    def get(self):
        args = parser.parse_args()
        return getMessages(args['lat'], args['lng'])

    def post(self):
        args = parser.parse_args()
        insertMessage(args['message'], args['lat'], args['lng'], args['deviceName'])
        return {'received': args['message']} 

class Rating(Resource):
    def post(self):
        args = parser.parse_args()
#        return args['ratingChanges']
        changeRating(args['ratingChanges'])

api.add_resource(Message, '/message')
api.add_resource(Rating, '/rating')

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8080, threaded=True)
