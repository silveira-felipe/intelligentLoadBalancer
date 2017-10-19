#
#   MIT License
#
#   Copyright (c) 2017.  Felipe Silveira
#
#   Permission is hereby granted, free of charge, to any person obtaining a copy
#   of this software and associated documentation files (the "Software"), to deal
#   in the Software without restriction, including without limitation the rights
#   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#   copies of the Software, and to permit persons to whom the Software is
#   furnished to do so, subject to the following conditions:
#
#   The above copyright notice and this permission notice shall be included in all
#   copies or substantial portions of the Software.
#
#   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
#   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
#   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
#   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
#   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
#  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
#  SOFTWARE.
#

FROM python:latest
MAINTAINER Felipe Silveira <felipesilveira.dev@gmail.com>

ENV HOME /home
RUN mkdir -p $HOME

RUN mkdir -p /intelligentLoadBalancer
RUN chmod 644 /intelligentLoadBalancer/

VOLUME /tmp

ADD ./build/docker/run.sh /run.sh
RUN chmod 755 /run.sh

ADD ./build/docker/ai.py /ai.py
RUN chmod 755 /run.sh

EXPOSE 8080 8080
EXPOSE 8000 8000

RUN pip install -U scikit-learn

CMD ["/run.sh"]

ADD target/intelligent-load-balancer-0.3.jar /intelligentLoadBalancer/intelligent-load-balancer-0.3.jar
RUN sh -c 'touch /intelligentLoadBalancer/intelligent-load-balancer-0.3.jar'
