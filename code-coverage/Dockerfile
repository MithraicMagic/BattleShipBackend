#
# Prepare Stage
#
FROM node:alpine

COPY package.json .
COPY package-lock.json .
COPY result ./result

EXPOSE 5000

#
# Host test results
#

RUN npm i --silent

CMD ["npm", "start"]